package org.wkh.arete;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

public class CodeEvaluationPOC {
    public static void main(String[] args) throws IOException, InterruptedException {
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
        final byte[] payload = (tempCodePath.toString() + "\n").getBytes();

        /* this performs like shit on Mac/Linux due to Java not implementing native file watching */
        final WatchService watchService = FileSystems.getDefault().newWatchService();
        codeDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;

        final ProcessBuilder builder = new ProcessBuilder(editorPath.toString(), tempCodePath.toString());
        builder.start();
        EvaluationClient client = new EvaluationClient();

        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(client.evaluatePath(payload));
            }
            key.reset();
        }

        watchService.close();
    }
}
