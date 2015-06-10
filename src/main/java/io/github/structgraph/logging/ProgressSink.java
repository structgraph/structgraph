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
