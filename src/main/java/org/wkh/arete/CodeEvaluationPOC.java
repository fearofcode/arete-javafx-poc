package org.wkh.arete;

import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryWatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

public class CodeEvaluationPOC {
    //public static long lastModified;

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("application.properties"));
        String editorPathProperty = properties.getProperty("editorPath");

        if (editorPathProperty == null) {
            System.err.println("Set editorPath property in application.properties and try again");
            return;
        }

        final Path editorPath = Paths.get(editorPathProperty);

        final Path codeDirectory = Files.createTempDirectory("code_eval");
        final Path tempCodePath = Files.createTempFile(codeDirectory, "arete", ".py");
        //File codeFile = tempCodePath.toFile();
        final byte[] payload = (tempCodePath.toString() + "\n").getBytes();

        final ProcessBuilder builder = new ProcessBuilder(editorPath.toString(), tempCodePath.toString());
        builder.start();
        EvaluationClient client = new EvaluationClient();

        DirectoryWatcher watcher = DirectoryWatcher.builder()
                .path(codeDirectory) // or use paths(directoriesToWatch)
                .listener(event -> {
                    System.out.println(client.evaluatePath(payload));
                })
                .logger(null)
                .fileHashing(false) // defaults to true
                // .logger(logger) // defaults to LoggerFactory.getLogger(DirectoryWatcher.class)
                // .watchService(watchService) // defaults based on OS to either JVM WatchService or the JNA macOS WatchService
                .build();
        watcher.watch();
    }
}
