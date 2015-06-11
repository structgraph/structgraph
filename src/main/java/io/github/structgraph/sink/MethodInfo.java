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
