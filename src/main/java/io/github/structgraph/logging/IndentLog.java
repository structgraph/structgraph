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
