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

/**
 * Created with IntelliJ IDEA.
 * User: patrik
 * Date: 4/14/15
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndentLog {
    private StringBuilder sb;
    private boolean silent;

    public IndentLog() {
        sb = new StringBuilder();
    }
    
    public IndentLog silent() {
        this.silent = true;
        return this;
    }

    public IndentLog(String prefix) {
        sb = new StringBuilder(prefix);
    }

    public IndentLog indent() {
        if (!silent) {
            sb.append("  ");
        }
        return this;
    }

    public IndentLog outdent() {
        if (!silent) {
            sb.setLength(sb.length() - 2);
        }
        return this;
    }

    public IndentLog log(Object... stuff) {
        if (silent) {
            return this;
        }
        StringBuilder out = new StringBuilder(sb);
        for(Object x : stuff) {
            out.append(x).append(" ");
        }
        System.out.println(out);
        return this;
    }
}
