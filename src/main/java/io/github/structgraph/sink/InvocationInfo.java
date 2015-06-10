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
public class InvocationInfo {
    private final String typeName;
    private final String name;
    private final String signature;
    private final String field;

    private InvocationInfo(Builder b) {
        this.typeName = b.typeName;
        this.name = b.name;
        this.signature = b.signature;
        this.field = b.field;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public String getField() {
        return field;
    }
    
    public static Builder build(String typeName, String name) {
        return new Builder(typeName, name);
    }

    public static class Builder {

        private String typeName;
        private String name;
        private String signature;
        private String field;

        private Builder(String typeName, String name) {
            super();
            this.typeName = typeName;
            this.name = name;
        }

        public Builder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public Builder field(String field) {
            this.field = field;
            return this;
        }

        public InvocationInfo build() {
            return new InvocationInfo(this);
        }
    }
}
