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
package io.github.structgraph.source;

import io.github.structgraph.sink.Sink;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.stream.Stream;
import io.github.structgraph.logging.IndentLog;
import io.github.structgraph.sink.AccessLevel;
import io.github.structgraph.sink.FieldInfo;
import io.github.structgraph.sink.InvocationInfo;
import io.github.structgraph.sink.MethodInfo;
import io.github.structgraph.sink.TypeInfo;

/**
 *
 * @author Patrik Duditš
 */
public class AsmCollector extends ClassVisitor {
    private final Sink sink;
    private final AnnotationCollector av;
    private String type;

    public AsmCollector(Sink s) {
        super(ASM5);
        this.sink = s;
        this.av = new AnnotationCollector();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      this.type = typeToClassName(name);
      sink.startType(TypeInfo.builder(type)
              .superClassName(typeToClassName(superName))
              .interfaces(Stream.of(interfaces).map(AsmCollector::typeToClassName).collect(toList()))
              .genericTypes(collectGenerics(signature, SignatureReader::accept)).build());
    }

    @Override
    public void visitEnd() {
        sink.endType();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        sink.startMethod(MethodInfo.builder(name).signature(desc).access(accessLevel(access)).build());
        class Visitor extends AnalyzerAdapter {
            class FieldRef {
                String name;
                int opcode;

                FieldRef(int opcode, String name) {
                    this.opcode = opcode;
                    this.name = name;
                }

                @Override
                public String toString() {
                    return "FieldRef{" +
                            "name='" + name + '\'' +
                            ", opcode=" + opcode +
                            '}';
                }
            }

            Visitor() {
                super(ASM5, type, access, name, desc, null );
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
                switch(opcode) {
                    case GETFIELD:
                    case GETSTATIC:
                        if (stack != null) {
                            // happened for  org.apache.commons.codec.binary.Base32 ...
                            stack.set(stack.size()-1, new FieldRef(opcode, name));
                        }
                        break;
                }

            }
            
            
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {                
                if (!(opcode == INVOKESPECIAL && "<init>".equals(name))) {
                    Object target = null;
                    if (opcode != INVOKESTATIC) {
                        Type[] args = Type.getArgumentTypes(desc);
                        target = stack != null ? stack.get(stack.size() - args.length - 1) : null;
                    }
                    InvocationInfo.Builder info = InvocationInfo.build(typeToClassName(owner), name)
                            .signature(desc);
                    if (target instanceof FieldRef) {
                        info.field(((FieldRef)target).name);
                    }
                    sink.calls(info.build());
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
            
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return av.start(desc);
            }
            
            @Override
            public void visitEnd() {
                sink.endMethod();
                super.visitEnd();
            }
        };
        Visitor v = new Visitor();
        return v;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        sink.startField(FieldInfo.builder(name, descToClassName(desc)).genericTypes(collectGenerics(signature, 
                SignatureReader::acceptType)).build());
        return new FieldVisitor(ASM5) {

            @Override
            public void visitEnd() {
                sink.endField();
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return av.start(desc);
            }

        };
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return av.start(desc);
    }

    private static String descToClassName(String desc) {
        return Type.getType(desc).getClassName();
    }

    private static String typeToClassName(String desc) {
        return Type.getObjectType(desc).getClassName();
    }

    class AnnotationCollector extends AnnotationVisitor {
        AnnotationCollector() {
           super(ASM5);
       }

        AnnotationVisitor start(String desc) {
            sink.startAnnotation(descToClassName(desc));
            return this;
        }

        @Override
        public void visit(String name, Object value) {
            if (value instanceof Type) {
                value = ((Type) value).getClassName();
            }
            sink.annotationParam(name, value);
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            sink.annotationParam(name, value);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            if (name == null) {
                sink.nestedAnnotationArrayElement(descToClassName(desc));
            } else {
                sink.nestedAnnotation(name, descToClassName(desc));
            }
            return this;
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            sink.nestedAnnotationArray(name);
            return this;
        }

        @Override
        public void visitEnd() {
            sink.endAnnotation();
        }
    }
    
    private List<String> collectGenerics(String signature, BiConsumer<SignatureReader, SignatureVisitor> method) {
        if (signature != null) {
            GenericsCollector collector = new GenericsCollector();
            method.accept(new SignatureReader(signature), collector);
            return collector.getTypes();
        } else {
            return Collections.emptyList();
        }
    }

    static class GenericsCollector extends SignatureVisitor {
        private IndentLog log = new IndentLog(":").silent(); // we may need this for diagnostics
        private final List<String> types = new ArrayList<>();
        private boolean topLevel = true;

        public GenericsCollector() {
            super(ASM5);
        }
        
        public List<String> getTypes() {
            return this.types;
        }

        @Override
        public void visitFormalTypeParameter(String name) {
            log.log("FormalTypeParameter",name);
        }

        private void add(String name) {
            types.add(typeToClassName(name));
        }

        @Override
        public SignatureVisitor visitClassBound() {
            log.log("ClassBound");
            topLevel = false;
            return this;
        }

        @Override
        public SignatureVisitor visitInterfaceBound() {
            log.log("InterfaceBound").indent();
            return this;
        }

        @Override
        public SignatureVisitor visitSuperclass() {
            log.log("Superclass");
            topLevel = true;
            return this;
        }

        @Override
        public SignatureVisitor visitInterface() {
            log.log("Interface");
            topLevel = true;
            return this;
        }

        @Override
        public SignatureVisitor visitParameterType() {
            log.indent().log("ParameterType");
            return this;
        }

        @Override
        public SignatureVisitor visitReturnType() {
            log.log("ReturnType");
            return this;
        }

        @Override
        public SignatureVisitor visitExceptionType() {
            log.log("ExceptionType");
            return this;
        }

        @Override
        public void visitBaseType(char descriptor) {
            log.log("BaseType", descriptor);
            super.visitBaseType(descriptor);
        }

        @Override
        public void visitTypeVariable(String name) {
            log.log("TypeVariable", name);
        }

        @Override
        public SignatureVisitor visitArrayType() {
            log.indent().log("ArrayType");
            return this;
        }

        @Override
        public void visitClassType(String name) {
            log.log("ClassType",name).indent();
            if (topLevel) {
                topLevel = false;
            } else {
                add(name);
            }
        }

        @Override
        public void visitInnerClassType(String name) {
            log.log("InnerClassType",name);
            add(name);
        }

        @Override
        public void visitTypeArgument() {
            log.log("TypeArgument");
        }

        @Override
        public SignatureVisitor visitTypeArgument(char wildcard) {
            log.log("TypeArgument", wildcard);
            return this;
        }

        @Override
        public void visitEnd() {
            log.outdent().log("-end-");
        }

    }
    
    private static AccessLevel accessLevel(int access) {
        if (hasFlag(access, ACC_PUBLIC)) {
            return AccessLevel.PUBLIC;
        } else if (hasFlag(access, ACC_PRIVATE)) {
            return AccessLevel.PRIVATE;
        } else if (hasFlag(access, ACC_PROTECTED)) {
            return AccessLevel.PROTECTED;
        } else {
            return AccessLevel.DEFAULT;
        }
    }
    
    private static boolean hasFlag(int test, int flag) {
        return (test & flag) == flag;
    }
}
