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
import io.github.structgraph.sink.FieldInfo;
import io.github.structgraph.sink.InvocationInfo;
import io.github.structgraph.sink.MethodInfo;
import io.github.structgraph.sink.TypeInfo;

/**
 *
 * @author Patrik Duditš
 */
public class DelegatingSink implements Sink {
    protected Sink delegate;
    
    protected DelegatingSink(Sink delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean interestedIn(String type) {
        return delegate.interestedIn(type);
    }

    @Override
    public void startType(TypeInfo typeInfo) {
        delegate.startType(typeInfo);
    }

    @Override
    public void endType() {
        delegate.endType();
    }

    @Override
    public void startField(FieldInfo info) {
        delegate.startField(info);
    }
    
    @Override
    public void endMethod() {
        delegate.endMethod();
    }

    @Override
    public void startMethod(MethodInfo method) {
        delegate.startMethod(method);
    }

    @Override
    public void calls(InvocationInfo info) {
        delegate.calls(info);
    }

    @Override
    public void endField() {
        delegate.endField();
    }

    @Override
    public void annotationParam(String name, Object value) {
        delegate.annotationParam(name, value);
    }

    @Override
    public void nestedAnnotation(String name, String type) {
        delegate.nestedAnnotation(name, type);
    }

    @Override
    public void nestedAnnotationArray(String name) {
        delegate.nestedAnnotationArray(name);
    }

    @Override
    public void nestedAnnotationArrayElement(String type) {
        delegate.nestedAnnotationArrayElement(type);        
    }

    @Override
    public void endAnnotation() {
        delegate.endAnnotation();
    }

    @Override
    public void startAnnotation(String type) {
        delegate.startAnnotation(type);
    }
}
