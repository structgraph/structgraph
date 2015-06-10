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
package io.github.structgraph.source;

import io.github.structgraph.source.AsmCollector;
import io.github.structgraph.sink.Sink;
import java.io.File;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.Type;

/**
 * Created with IntelliJ IDEA.
 * User: patrik
 * Date: 3/28/15
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarCollector {
    private final AsmCollector collector;
    private Sink s;

    public JarCollector(Sink s) {
        this.s = s;
        this.collector = new AsmCollector(s);
    }


    public void walk(Path jarFile) throws IOException {
        walk(jarFile.toFile());
    }

    public void walk(File jarFile) throws IOException {
        ZipFile z = new ZipFile(jarFile);
        walk(z);
    }

    private static Pattern ARCHIVE = Pattern.compile("\\.[jwer]ar$");

    private interface InputStreamSupplier {
        InputStream get() throws IOException;
    }

    private void walk(ZipFile z) throws IOException {
        for(ZipEntry e :  Collections.list(z.entries())) {
            processEntry(() -> z.getInputStream(e), e);
        }
    }

    private void processEntry(InputStreamSupplier isProvider, ZipEntry e) throws IOException {
        if (e.getName().endsWith(".class")) {
            String className = e.getName().substring(0, e.getName().lastIndexOf('.'));
            className = Type.getObjectType(className).getClassName();
            if (s.interestedIn(className)) {
                try (InputStream in = isProvider.get()) {
                    ClassReader reader = new ClassReader(in);
                    reader.accept(collector, ClassReader.EXPAND_FRAMES);
                }
            }
        } else if (ARCHIVE.matcher(e.getName()).find()) {
            try (ZipInputStream in = new ZipInputStream(isProvider.get())) {
                walk(in);
            }
        }
    }

    private void walk(ZipInputStream zis) throws IOException {
        for(ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
           processEntry(() -> nonClosing(zis), entry);
        }
    }

    private static InputStream nonClosing(InputStream is) {
        return new InputStream() {
            public int read() throws IOException {
                return is.read();
            }

            public int read(byte[] b) throws IOException {
                return is.read(b);
            }

            public int read(byte[] b, int off, int len) throws IOException {
                return is.read(b, off, len);
            }

            public long skip(long n) throws IOException {
                return is.skip(n);
            }

            public int available() throws IOException {
                return is.available();
            }

            public void close() throws IOException {

            }

            public void mark(int readlimit) {
                is.mark(readlimit);
            }

            public void reset() throws IOException {
                is.reset();
            }

            public boolean markSupported() {
                return is.markSupported();
            }
        };
    }
}
