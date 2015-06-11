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
package io.github.structgraph;

import io.github.structgraph.logging.LoggingSink;
import io.github.structgraph.source.AsmCollector;
import io.github.structgraph.sink.Sink;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static io.github.structgraph.NestedMatcher.passes;
import static io.github.structgraph.NestedMatcher.passing;
import io.github.structgraph.sink.TypeInfo;

import static org.mockito.Mockito.*;

import org.mockito.InOrder;
import org.objectweb.asm.ClassReader;

import io.github.structgraph.testcode.CallInSwitch;
import io.github.structgraph.testcode.ChainedCall;
import io.github.structgraph.testcode.Inheritance;
import io.github.structgraph.testcode.SampleMDB;
import static org.hamcrest.Matchers.*;
import org.hamcrest.Matcher;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Patrik Duditš
 */
public class AsmCollectorTest {
    
    @Test
    public void logTree() throws IOException {
        ClassReader classReader = new ClassReader(SampleMDB.class.getCanonicalName());
        AsmCollector visitor = new AsmCollector(new LoggingSink());
        classReader.accept(visitor,ClassReader.EXPAND_FRAMES);
        new ClassReader(SampleMDB.class.getCanonicalName()+"$Parametrized").accept(visitor, ClassReader.EXPAND_FRAMES);
        new ClassReader(SampleMDB.class.getCanonicalName()+"$Parametrized2").accept(visitor, ClassReader.EXPAND_FRAMES);
    }
    
    @Test
    public void testSwitchCall() throws IOException {
        ClassReader classReader = new ClassReader(CallInSwitch.class.getCanonicalName());
        Sink mock = mock(Sink.class);
        classReader.accept(new AsmCollector(mock),ClassReader.EXPAND_FRAMES);
        InOrder order = inOrder(mock);
        
        order.verify(mock).startMethod(passing((info) -> assertEquals("process", info.getName())));
        order.verify(mock).calls(passing((info) -> assertEquals("option1", info.getName())));
        order.verify(mock).calls(passing((info) -> assertEquals("option2", info.getName())));
        order.verify(mock).endMethod();
        verify(mock, never()).calls(passing((info) -> assertEquals("<init>", info.getName())));
    }    
    
    @Test
    public void testChainedCall() throws IOException {
        ClassReader classReader = new ClassReader(ChainedCall.class.getCanonicalName());
        Sink mock = mock(Sink.class);
        classReader.accept(new AsmCollector(mock),ClassReader.EXPAND_FRAMES);
        
        InOrder order = inOrder(mock);
        order.verify(mock).startMethod(passing((info) -> assertEquals("process", info.getName())));        
        order.verify(mock).calls(passing((info) -> assertEquals("getIntf", info.getName())));
        order.verify(mock).calls(passing((info) -> assertEquals("method", info.getName())));
        order.verify(mock).endMethod();
    }
    
    @Test
    public void testInheritanceAndGenerics() throws IOException {
        ClassReader classReader = new ClassReader(Inheritance.class.getCanonicalName());
        Sink mock = mock(Sink.class);
        classReader.accept(new AsmCollector(mock),ClassReader.EXPAND_FRAMES);
        
        verify(mock).startType(argThat(passes((info) -> {
            assertEquals("java.util.AbstractMap", info.getSuperClassName());
            assertEquals(Collections.singletonList("java.lang.Comparable"), info.getInterfaces());
            assertEquals(Arrays.asList("java.lang.String", "io.github.structgraph.testcode.Inheritance", 
                        "io.github.structgraph.testcode.Inheritance"), info.getGenericTypes());
        })));
        verify(mock).startField(argThat(passes((info) -> {
            assertEquals("aMap", info.getName());
            assertEquals("java.util.Map", info.getType());
            assertEquals(Arrays.asList("java.lang.Integer", "java.lang.Number"), info.getGenericTypes());
        })));
    } 
    
    public static void parse(Class<?> clazz, AsmCollector sink) throws IOException {
        ClassReader classReader = new ClassReader(clazz.getCanonicalName());
        classReader.accept(sink,ClassReader.EXPAND_FRAMES);        
    }
    
}
