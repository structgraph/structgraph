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
            assertEquals(Arrays.asList("java.lang.String", "net.dudits.structgraph.testcode.Inheritance", 
                        "net.dudits.structgraph.testcode.Inheritance"), info.getGenericTypes());
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
