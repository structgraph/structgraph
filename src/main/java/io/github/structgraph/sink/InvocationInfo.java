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
