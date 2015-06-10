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
public class MethodInfo {
    private final String name;
    private final String signature;
    private final AccessLevel accessLevel;

    private MethodInfo(Builder b) {
        this.name = b.name;
        this.signature = b.signature;
        this.accessLevel = b.accessLevel;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
    
    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private String name;
        private String signature;
        private AccessLevel accessLevel;

        private Builder(String name) {
            super();
            this.name = name;
        }

        public Builder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public Builder access(AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
            return this;
        }

        public MethodInfo build() {
            return new MethodInfo(this);
        }
    }
    
    
}
