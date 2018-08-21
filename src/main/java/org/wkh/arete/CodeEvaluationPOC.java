package org.wkh.arete;

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

        System.out.println(tempCodePath);

        final WatchService watchService = FileSystems.getDefault().newWatchService();
        codeDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;

        final String editCmd = String.format("\"%s\" \"%s\" ", editorPath.toString(), tempCodePath.toString());

        Runtime.getRuntime().exec(editCmd);
        // warm up the client and the server
        ipcEvaluateCode("warmup\n");

        /* handle phenomenon of two edit events occurring: date modified and content changed */
        /* sometimes this doesn't trigger? wtf? */
        int eventCount = 0;

        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                eventCount++;

                if (eventCount >= 2) {
                    ipcEvaluateCode(tempCodePath.toString());
                    eventCount = 0;
                }
            }
            key.reset();
        }

        watchService.close();
    }

    private static void ipcEvaluateCode(String tempCodePath) throws IOException, InterruptedException {
        System.out.println("evaluating...");
        long start = System.nanoTime();

        System.out.println(EvaluationClient.evaluatePath(tempCodePath));
        long end = System.nanoTime();
        long elapsedMs = (end - start) / 1_000_000;
        System.out.println("Ran in " + elapsedMs + "ms");
    }
}
