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
package io.github.structgraph.neo4j;

import io.github.structgraph.neo4j.Graph;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Patrik Duditš
 */
public class DisplayNameTest {
    
    @Test
    public void testSeparateCamel() {
        assertEquals("camel Works",Graph.separateCamel("camelWorks"));
        assertEquals("Static Group Service Bean", Graph.separateCamel("StaticGroupServiceBean"));
    }
    
    @Test
    public void testCapticalCamel() {
        assertEquals("DC Notificator",Graph.separateCamel("DCNotificator"));
    }    
    
}
