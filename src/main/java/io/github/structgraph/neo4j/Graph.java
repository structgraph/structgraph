/* 
 * Copyright (C) 2015 Patrik Duditš <structgraph@dudits.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.structgraph.neo4j;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.Schema;

import java.util.*;
import java.util.function.Consumer;
import io.github.structgraph.sink.AccessLevel;
import io.github.structgraph.sink.MethodInfo;

/**
 * Created with IntelliJ IDEA. User: patrik Date: 3/28/15 Time: 10:20 PM To
 * change this template use File | Settings | File Templates.
 */
public class Graph {

    private final String typeName;
    private Node methodNode;


    enum NodeLabel implements Label {

        Class, Method, Field, Annotation, AnnotationValue, Invocation;
    }

    enum ClassRel implements RelationshipType {        
        hasField, hasMethod, inherits, hasTypeParameter;
    }

    enum MethodRel implements RelationshipType {

        invokes;
    }

    enum FieldRel implements RelationshipType {

        hasType;
    }

    enum AnnotationRel implements RelationshipType {

        annotated, hasType, param;
    }

    enum InvocationRel implements RelationshipType {

        ofClass, method, onField;
    }

    class AnnotationNode {

        Node node;
        String param;

        AnnotationNode(Node n) {
            this.node = n;
        }

        AnnotationNode(Node n, String param) {
            this.node = n;
            this.param = param;
        }

        AnnotationNode withParam(String param) {
            return new AnnotationNode(node, param);
        }
    }

    private final GraphDatabaseService db;
    private final Node typeNode;
    private Map<String, Node> fields = new HashMap<>();
    private Node annotationTarget;
    private Deque<AnnotationNode> annotationStack = new LinkedList<>();

    private Graph(GraphDatabaseService db, Node node) {
        this.db = db;
        this.typeNode = node;
        this.typeName = typeNode.getProperty("name").toString();
        annotationTarget = node;
    }

    public static Graph ofType(GraphDatabaseService db, String name) {
        return new Graph(db, node(db, NodeLabel.Class, name, null));
    }

    private static Node node(GraphDatabaseService db, Label type, String name, Consumer<Node> customizer) {
        Node result = db.findNode(type, "name", name);

        if (result != null) {
            return result;
        } else {
            result = db.createNode(type);
            result.setProperty("name", name);
            result.setProperty("simpleName", simpleName(name));
            result.setProperty("displayName", separateCamel(simpleName(name)));
            if (customizer != null) {
                customizer.accept(result);
            }
            return result;
        }
    }

    private static String simpleName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private Node node(Label type, String name) {
        return node(db, type, name, null);
    }

    private Node node(Label type, String name, Consumer<Node> customizer) {
        return node(db, type, name, customizer);
    }

    public void addField(String name, String typeName, List<String> genericTypes) {
        Node fieldNode = db.createNode(NodeLabel.Field);
        fieldNode.setProperty("name", name);
        fieldNode.setProperty("displayName", separateCamel(name));
        typeNode.createRelationshipTo(fieldNode, ClassRel.hasField);
        Node fieldType = node(NodeLabel.Class, typeName);
        fieldNode.createRelationshipTo(fieldType, FieldRel.hasType);
        fields.put(name, fieldNode);
        annotationTarget(fieldNode);
        addGenerics(fieldNode, genericTypes);
    }

    private void annotationTarget(Node node) {
        this.annotationTarget = node;
    }

    public void inherits(String type) {
        Node intNode = node(NodeLabel.Class, type);
        typeNode.createRelationshipTo(intNode, ClassRel.inherits);        
    }
    
    public void inherits(Collection<String> types) {
        types.stream().forEach(this::inherits);
    }
    
    public void generics(List<String> genericTypes) {
        addGenerics(annotationTarget, genericTypes);
    }
        
    private void addGenerics(Node source, List<String> genericTypes) {
        int index=0;
        for (String type : genericTypes) {
            Node intNode = node(NodeLabel.Class, type);
            Relationship r = source.createRelationshipTo(intNode, ClassRel.hasTypeParameter);
            r.setProperty("index", ++index);
        }
    }
    

    public void addMethod(MethodInfo info) {
        methodNode = methodNode(typeName, info.getName(), info.getSignature());
        applyAccess(methodNode, info.getAccessLevel());
        typeNode.createRelationshipTo(methodNode, ClassRel.hasMethod);
        annotationTarget(methodNode);
    }
    
    private void applyAccess(Node node, AccessLevel level) {
        String access = level.name().toLowerCase();
        node.setProperty("access", access);
        node.setProperty(access, true);
    }

    private Node methodNode(String owner, String name, String signature) {
        return node(NodeLabel.Method, owner + "#" + name + signature, (n) -> {
            n.setProperty("methodName", name);
            n.setProperty("signature", signature);
            n.setProperty("simpleName", simpleName(owner) + "#" + name);
            n.setProperty("displayName", separateCamel(name));
        });
    }

    public void methodCalls(String owner, String name, String signature, String field) {
        Node invNode = db.createNode(NodeLabel.Invocation);
        methodNode.createRelationshipTo(invNode, MethodRel.invokes);
        Node targetMethod = methodNode(owner, name, signature);
        invNode.createRelationshipTo(targetMethod, InvocationRel.method);
        Node targetType = node(NodeLabel.Class, owner);
        invNode.createRelationshipTo(targetType, InvocationRel.ofClass);

        // let's add some shortcut yet.
        Relationship rel = methodNode.createRelationshipTo(targetMethod, MethodRel.invokes);
        if (field != null && fields.containsKey(field)) {
            Node fieldNode = fields.get(field);
            // we may miss the key for some synthetic fields or enums.
            invNode.createRelationshipTo(fieldNode, InvocationRel.onField);
            Iterable<Relationship> annotations = fieldNode.getRelationships(AnnotationRel.annotated);
            for (Iterator<Relationship> i = annotations.iterator(); i.hasNext();) {
                if (rel == null) {
                    rel = methodNode.createRelationshipTo(targetMethod, MethodRel.invokes);
                }
                Node av = i.next().getEndNode();
                Node at = av.getSingleRelationship(AnnotationRel.hasType, Direction.OUTGOING).getEndNode();
                rel.setProperty("annotated", at.getProperty("name"));
                rel = null;
            }
        }
    }

    public Node startAnnotation(String name) {
        Node annotationNode = node(NodeLabel.Annotation, name);
        Node value = db.createNode(NodeLabel.AnnotationValue);
        value.setProperty("name", annotationNode.getProperty("simpleName"));
        value.createRelationshipTo(annotationNode, AnnotationRel.hasType);
        if (annotationStack.isEmpty()) {
            Relationship rel = annotationTarget.createRelationshipTo(value, AnnotationRel.annotated);
            rel.setProperty("name", name);
            final String simpleName = simpleName(name);
            rel.setProperty("w", simpleName);
            annotationTarget.addLabel(()->simpleName);
        }
        annotationStack.push(new AnnotationNode(value));
        return value;
    }

    public void nestedAnnotation(String name, String type) {
        if (type == null) {
            // we duplicate the stack head, as there will be to endAnnotation calls for this
            // nested array annotation
            annotationStack.push(annotationStack.peek().withParam(name));
        } else {
            String paramName = name != null ? name : annotationStack.peek().param;
            if (paramName == null) {
                throw new IllegalStateException("Something went wrong with annotation nesting");
            }
            Node parent = annotationStack.peek().node;
            Node av = startAnnotation(type);
            Relationship rel = parent.createRelationshipTo(av, AnnotationRel.param);
            rel.setProperty("name", paramName);
        }
    }

    public void annotationParam(String name, Object value) {
        final Node node = annotationStack.peek().node;
        node.setProperty(name, value);
        if ("value".equals(name) && annotationStack.size() == 1) {
            Relationship rel = node.getSingleRelationship(AnnotationRel.annotated, Direction.INCOMING);
            annotationTarget.setProperty(String.valueOf(rel.getProperty("w")), value);
        }
    }

    public void endAnnotation() {
        annotationStack.pop();
    }

    public static void prepareSchema(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Schema schema = db.schema();
            schema.indexFor(NodeLabel.Class).on("name").create();
            schema.indexFor(NodeLabel.Method).on("name").create();
            schema.indexFor(NodeLabel.Annotation).on("name").create();
            tx.success();
        }
    }

    public static void finalizeSchema(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            linkImplementations(db);
            //labelEjbs(db);
            tx.success();
        }
        // following does so many changes, it's better to give it separate TX
        try (Transaction tx = db.beginTx()) {
            findVirtualMethods(db);
            replaceVirtualMethods(db);
            tx.success();
        }

    }

    private static void replaceVirtualMethods(GraphDatabaseService db) throws QueryExecutionException {
        // there are only two types of relationships in these methods. We could actually make a query for those
        for (String relType : new String[] {"invokes","method"}) {
            // and we will copy them. relationship type cannot be parametrized, therefore we enumerated them
            db.execute(String.format(
                    "match (m)-[:replaceBy]->(sm), (a)-[r:%1$s]->m\n" +
                            "create a-[ri:%1$s]->sm\n" +
                            "set ri=r",relType));
        }
        // and now we can finally delete the old method node
        db.execute("match (m)-[rep:replaceBy]->(), ()-[r]->m delete r, rep, m");
    }

    private static void findVirtualMethods(GraphDatabaseService db) throws QueryExecutionException {
        db.execute(// find methods that are not declared in the class
                "match (m:Method) where not m<-[:hasMethod]-()\n" +
                        // method.name is Class#name(signature)
                        "with m, split(m.name,'#')[0] as className\n" +
                        // find a method with same name and signature
                        "match (superMethod:Method{methodName:m.methodName, signature:m.signature}),\n" +
                        // which is declared in a superclass
                        "      p=(c:Class{name:className})-[:inherits*]->(super)-[:hasMethod]->superMethod\n" +
                        // and group them in a collection
                        "with m, collect(p) as allPossibleSuperMethods\n" +
                        // and now keep the closest method (with the shortest path to it)
                        "with m, reduce(path = null, p in allPossibleSuperMethods | \n" +
                        "          case \n" +
                        // the candidate is either better than nothing or shorter than previous
                        "            when path is null or length(p) < length(path) then p \n" +
                        // or we've got our winner already
                        "            else path \n" +
                        "          end) as superMethodPath\n" +
                        // and the last on the path is the closest declared method
                        "with m,last(superMethodPath) as superMethod\n" +
                        // so we mark the replacement
                        "create m-[:replaceBy]->superMethod");
    }

    private static void labelEjbs(GraphDatabaseService db) throws QueryExecutionException {
        // tag EJBs with their type. Labels cannot be dynamic or parametrized
        // in cypher, therefore the concatenation.
        for (String type : new String[] {"Singleton", "Stateless", "Stateful", "MessageDriven"}) {
            db.execute(String.format("match ()<-[sar:annotated]-(bean:Class)\n" +
                    "where sar.name = \"javax.ejb.%1$s\"\n" +
                    "set bean:%1$s",type));
        }
    }

    private static void linkImplementations(GraphDatabaseService db) throws QueryExecutionException {
        // create link from definition to implementation
        db.execute("match (m:Method)<-[:hasMethod]-(i:Class)-[:inherits*..4]->(c:Class)-[:hasMethod]->(d:Method)\n"
                + "where d.methodName = m.methodName AND d.signature = m.signature\n"
                + "create d-[:overriden]->m");
        // count probability of implementation method referred
        db.execute("match (d:Method)-[r:overriden]->()\n"
                + "with d, count(r) as c\n"
                + "match d-[r:overriden]->()\n"
                + "set r.p = 1.0/c");
    }
    
    static String separateCamel(String s) {
        return s.replaceAll("(\\p{javaLowerCase})(\\p{javaUpperCase})","$1 $2")
                .replaceAll("(\\p{javaUpperCase}+)(\\p{javaUpperCase})(\\p{javaLowerCase}+)", "$1 $2$3");
    }

}
