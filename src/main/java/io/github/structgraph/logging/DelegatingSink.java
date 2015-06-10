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
