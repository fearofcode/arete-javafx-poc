package org.wkh.arete;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class CodeEvaluationPOC {
    public static void main(String[] args) throws IOException, InterruptedException {
        String editorPathProperty = System.getProperty("editorPath");
        if (editorPathProperty == null) {
            System.err.println("Set editorPath property and try again");
            return;
        }

        final Path editorPath = Paths.get(editorPathProperty);

        final Path codeDirectory = Files.createTempDirectory("code_eval");
        final Path tempCodePath = Files.createTempFile(codeDirectory, "arete", ".py");
        final File tempCodeFile = tempCodePath.toFile();

        System.out.println(tempCodePath);

        final WatchService watchService = FileSystems.getDefault().newWatchService();
        codeDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;

        final String editCmd = String.format("\"%s\" \"%s\" ", editorPath.toString(), tempCodePath.toString());

        Runtime.getRuntime().exec(editCmd);
        // warm up the client and the server
        EvaluationClient.evaluatePath("warmup\n");

        /* shitty hack to handle mostly two events getting fired but sometimes only one occurs when saving files */
        long lastModified = 0;

        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                long newLastModified = tempCodeFile.lastModified();

                if (newLastModified > lastModified) {
                    System.out.println(EvaluationClient.evaluatePath(tempCodePath.toString()));
                    lastModified = newLastModified;
                }
            }
            key.reset();
        }

        watchService.close();
    }
}
