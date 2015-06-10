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

import io.github.structgraph.logging.ProgressSink;
import io.github.structgraph.source.JarCollector;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import io.github.structgraph.neo4j.Graph;
import io.github.structgraph.neo4j.Neo4jSink;

/**
 *
 * @author Patrik Duditš
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            printUsage();
            System.exit(1);
        }
        File jar = new File(args[0]);
        if (!jar.exists()) {
            System.err.println(jar + " does not exist");
            System.exit(1);
        }

        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(args[1]);
        Runtime.getRuntime().addShutdownHook(new Thread(db::shutdown));
        Graph.prepareSchema(db);

        ProgressSink sink = new ProgressSink(new Neo4jSink(db, s -> s.startsWith("com.rwe.icon"))) {

            @Override
            protected void typesProcessed(int typeCounter) {
                if (typeCounter % 100 == 0) {
                    System.out.printf("%d classes processed...\n", typeCounter);
                }
            }

        };

        System.out.println("Processing "+jar);
        long start = System.currentTimeMillis();
        new JarCollector(sink).walk(jar);
        System.out.println("Processed " + sink.getNumTypesProcessed() + " classes in " + secondsSince(start) + "s.");
        start = System.currentTimeMillis();
        System.out.print("Inferring additional information... ");
        Graph.finalizeSchema(db);
        System.out.println("Done in "+secondsSince(start)+ "s.");
    }

    private static long secondsSince(long start) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start);
    }

    private static void printUsage() {
        System.err.println("Usage: java -jar structgraph.jar <java archive> <location of neo4j databasee to create>");
    }
}
