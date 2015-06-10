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
package io.github.structgraph.sink;

/**
 *
 * @author Patrik Duditš
 */
public interface Sink {
    boolean interestedIn(String type);
    
    void startType(TypeInfo typeInfo);

    void endType();

    public void endMethod();

    public void startMethod(MethodInfo info);

    public void calls(InvocationInfo info);

    public void startField(FieldInfo info);

    public void endField();

    void annotationParam(String name, Object value);

    /**
     * Start of annotation parameter. This is quite rare case, where a parameter expects single annotation. Calls to
     * {@link #annotationParam} will follow concluded with call to {@link #endAnnotation()}.
     * @param name name of parameter.
     * @param type type of parameter.
     */
    void nestedAnnotation(String name, String type);
    
    /**
     * Start of annotation array parameter. At least one call to {@link #nestedAnnotationArrayElement} will follow,
     * with call to {@link #endAnnotation() } next.
     * @param name the name of annotation parameter
     */
    void nestedAnnotationArray(String name);
    
    /**
     * Start of element of annotation array. Calls to {@link #annotationParam} will follow concluded with call to 
     * {@link #endAnnotation()}.
     * @param type the type of annotation
     */
    void nestedAnnotationArrayElement(String type);
    
    void endAnnotation();

    void startAnnotation(String type);
}
