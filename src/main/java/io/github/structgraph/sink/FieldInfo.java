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
public class FieldInfo {
    private final String name;
    private final String type;
    private final List<String> genericTypes;
    
    private FieldInfo(Builder b) {
        this.name = b.name;
        this.type = b.type;
        this.genericTypes = b.genericTypes == null ? Collections.emptyList() : new ArrayList<>(b.genericTypes);   
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<String> getGenericTypes() {
        return genericTypes;
    }
    
    public static Builder builder(String name, String type) {
        return new Builder(name, type);
    }

    public static class Builder {

        private String name;
        private String type;
        private List<String> genericTypes;

        private Builder(String name, String type) {
            super();
            this.name = name;
            this.type = type;
        }

        public Builder genericTypes(List<String> genericTypes) {
            this.genericTypes = genericTypes;
            return this;
        }

        public FieldInfo build() {
            return new FieldInfo(this);
        }
    }
    
}
