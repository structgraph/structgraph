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
package io.github.structgraph.sink;

/**
 *
 * @author Patrik Duditš
 */
public interface Sink {
    boolean interestedIn(String type);
    
    void startType(TypeInfo typeInfo);
    void endType();
    
    public void startMethod(MethodInfo info);
    public void calls(InvocationInfo info);
    public void endMethod();
    

    public void startField(FieldInfo info);
    public void endField();

    void startAnnotation(String type);
    
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

}
