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
package io.github.structgraph.logging;

import io.github.structgraph.sink.Sink;
import io.github.structgraph.sink.TypeInfo;

/**
 *
 * @author Patrik Duditš
 */
public class ProgressSink extends DelegatingSink {
    private int numTypesProcessed;

    public ProgressSink(Sink delegate) {
        super(delegate);
    }

    @Override
    public void startType(TypeInfo typeInfo) {
        super.startType(typeInfo);
        numTypesProcessed++;
        typesProcessed(numTypesProcessed);
    }

    protected void typesProcessed(int typeCounter) {
    }

    public int getNumTypesProcessed() {
        return numTypesProcessed;
    }
    
}
