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
