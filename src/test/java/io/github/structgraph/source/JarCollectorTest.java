/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.structgraph.source;

import io.github.structgraph.sink.Sink;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Andrea Gencova
 */
public class JarCollectorTest {

    @Test
    public void warFileIsProcessed() throws IOException {
        Sink s = mock(Sink.class);
        JarCollector c = new JarCollector(s);

        c.walk(new File("src\\test\\archives\\WebApp.war"));
        verify(s).interestedIn("aaa.Servlet");
    }
}
