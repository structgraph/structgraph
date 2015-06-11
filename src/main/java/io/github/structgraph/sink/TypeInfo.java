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
