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
