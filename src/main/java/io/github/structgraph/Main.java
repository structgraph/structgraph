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
        if (args.length != 3) {
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

        ProgressSink sink = new ProgressSink(new Neo4jSink(db, s -> s.startsWith(args[2]))) {

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
        System.err.println("Usage: java -jar structgraph.jar <java archive> <location of neo4j databasee to create> <package prefix>");
    }
}
