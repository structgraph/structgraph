/* 
 * Copyright 2015 Patrik Duditš <structgraph@dudits.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.structgraph.logging;

import io.github.structgraph.sink.Sink;
import java.util.List;
import java.util.function.Predicate;
import io.github.structgraph.sink.FieldInfo;
import io.github.structgraph.sink.InvocationInfo;
import io.github.structgraph.sink.MethodInfo;
import io.github.structgraph.sink.TypeInfo;

/**
 *
 * @author Patrik Duditš
 */
public class LoggingSink implements Sink {
    private final Predicate<String> typeFilter;
    private final IndentLog log = new IndentLog();

    public LoggingSink() {
        this.typeFilter = (s) -> true;
    }

    public LoggingSink(Predicate<String> typeFilter) {
        this.typeFilter = typeFilter;
    }

    @Override
    public boolean interestedIn(String type) {
        return typeFilter.test(type);
    }

    @Override
    public void startType(TypeInfo typeInfo) {
        log("startType",typeInfo.getTypeName());
        indent();
        log("extends", typeInfo.getSuperClassName());
        logList("implements", typeInfo.getInterfaces());
        logList("generics", typeInfo.getGenericTypes());
    }

    @Override
    public void endType() {
        outdent();
    }
    
    private void logList(String title, List<String> list) {
        if (list != null && !list.isEmpty()) {
            log(title);
            indent();
            log(list.toArray());
            outdent();
        }
    }

    @Override
    public void startField(FieldInfo info) {
        log("field", info.getName(), info.getType());
        logList("generics", info.getGenericTypes());
        indent();
    }

    @Override
    public void endMethod() {
        outdent();
    }

    @Override
    public void startMethod(MethodInfo method) {
        log("startMethod", method.getAccessLevel(), method.getName(), method.getSignature());
        indent();
    }

    @Override
    public void calls(InvocationInfo info) {
        log("calls", info.getTypeName(), info.getName(), info.getField());
    }

    @Override
    public void endField() {
        outdent();
    }

    @Override
    public void startAnnotation(String type) {
        log("Annotation", type);
        indent();
    }

    @Override
    public void annotationParam(String name, Object value) {
        log("param", name, value, value.getClass().getName());
    }

    @Override
    public void nestedAnnotation(String name, String type) {
        log("nest",name, type);
        indent();
    }

    @Override
    public void nestedAnnotationArray(String name) {
        log("nest[]", name);
        indent();
    }

    @Override
    public void nestedAnnotationArrayElement(String type) {
        log("element", type);
        indent();
    }

    @Override
    public void endAnnotation() {
        outdent();
    }

    private void indent() {
        log.indent();
    }
    
    private void outdent() {
        log.outdent();
    }
    
    private void log(Object... stuff) {
        log.log(stuff);
    }
    
}
