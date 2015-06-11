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

import java.util.List;
import io.github.structgraph.sink.Sink;
import org.neo4j.graphdb.*;

import java.util.function.Predicate;
import io.github.structgraph.sink.FieldInfo;
import io.github.structgraph.sink.InvocationInfo;
import io.github.structgraph.sink.MethodInfo;
import io.github.structgraph.sink.TypeInfo;

/**
 * Created with IntelliJ IDEA.
 * User: patrik
 * Date: 3/28/15
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class Neo4jSink implements Sink, AutoCloseable {
    private final GraphDatabaseService db;
    private final Predicate<String> typeFilter;
    private Transaction tx;
    private Graph typeNode;

    public Neo4jSink(GraphDatabaseService db) {
        this.db = db;
        this.typeFilter = (s) -> true;
    }

    public Neo4jSink(GraphDatabaseService db, Predicate<String> typeFilter) {
        this.db = db;
        this.typeFilter = typeFilter;
    }

    @Override
    public void close() throws Exception {
        if (tx != null) {
            tx.failure();
            tx.close();
        }
    }

    @Override
    public boolean interestedIn(String type) {
        return typeFilter.test(type);
    }

    @Override
    public void startType(TypeInfo typeInfo) {
        tx = db.beginTx();
        typeNode = Graph.ofType(db, typeInfo.getTypeName());
        if (typeInfo.getSuperClassName() != null) {
            typeNode.inherits(typeInfo.getSuperClassName());
        }
        typeNode.inherits(typeInfo.getInterfaces());
        typeNode.generics(typeInfo.getGenericTypes());        
    }

    @Override
    public void endType() {
        tx.success();
        tx.close();
        tx=null;
    }


    @Override
    public void startField(FieldInfo info) {
        typeNode.addField(info.getName(), info.getType(), info.getGenericTypes());
    }
    
    @Override
    public void endMethod() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void startMethod(MethodInfo method) {
        typeNode.addMethod(method);
    }

    @Override
    public void calls(InvocationInfo info) {
        typeNode.methodCalls(info.getTypeName(), info.getName(), info.getSignature(), info.getField());
    }

    @Override
    public void endField() {
    }

    @Override
    public void annotationParam(String name, Object value) {
        typeNode.annotationParam(name != null ? name : "value", value);
    }

    @Override
    public void nestedAnnotation(String name, String type) {
        typeNode.nestedAnnotation(name, type);
    }

    @Override
    public void nestedAnnotationArray(String name) {
        typeNode.nestedAnnotation(name, null);
    }

    @Override
    public void nestedAnnotationArrayElement(String type) {
        typeNode.nestedAnnotation(null, type);
    }

    @Override
    public void endAnnotation() {
        typeNode.endAnnotation();
    }

    @Override
    public void startAnnotation(String type) {
        typeNode.startAnnotation(type);
    }
}
