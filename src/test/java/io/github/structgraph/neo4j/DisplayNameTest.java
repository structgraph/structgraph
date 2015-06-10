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
