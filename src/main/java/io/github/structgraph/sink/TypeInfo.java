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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Patrik Duditš
 */
public class TypeInfo {
    private final String typeName;
    private final String simpleName;
    private final String superClassName;
    private final List<String> interfaces;
    private final List<String> genericTypes;

    private TypeInfo(Builder b) {
        this.simpleName = b.simpleName;
        this.superClassName = b.superClassName;
        this.interfaces = Collections.unmodifiableList(new ArrayList<>(b.interfaces));
        this.genericTypes = Collections.unmodifiableList(new ArrayList<>(b.genericTypes));
        this.typeName = b.typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public List<String> getGenericTypes() {
        return genericTypes;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getSuperClassName() {
        return superClassName;
    }
    
    public static Builder builder(String typeName) {
        return new Builder(typeName);
    }

    public static class Builder {

        private final String typeName;
        private final String simpleName;
        private String superClassName;
        private List<String> interfaces;
        private List<String> genericTypes;

        private Builder(String typeName) {
            this.typeName = typeName;
            this.simpleName = typeName.substring(typeName.lastIndexOf('.') + 1);
        }

        public Builder superClassName(String superClassName) {
            this.superClassName = superClassName;
            return this;
        }

        public Builder interfaces(List<String> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder genericTypes(List<String> genericTypes) {
            this.genericTypes = genericTypes;
            return this;
        }

        public TypeInfo build() {
            return new TypeInfo(this);
        }
    }
    
}
