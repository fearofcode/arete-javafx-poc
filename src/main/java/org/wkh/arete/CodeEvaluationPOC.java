package org.wkh.arete;

import java.io.IOException;
import java.nio.file.*;

public class CodeEvaluationPOC {

    public static void main(String[] args) throws IOException, InterruptedException {
        String evaluatorPathProperty = System.getProperty("evaluatorPath");
        if (evaluatorPathProperty == null) {
            System.err.println("Set evaluatorPath property and try again");
            return;
        }

        final Path evaluatorPath = Paths.get(evaluatorPathProperty);

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
        codeDirectory.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;

        final String editCmd = String.format("\"%s\" \"%s\" ", editorPath.toString(), tempCodePath.toString());

        Runtime.getRuntime().exec(editCmd);

        /* the editor will change both content and last modified timestamp. wait for two events, then process */
        int modifyEventCount = 0;

        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    modifyEventCount++;

                    if (modifyEventCount >= 2) {
                        evaluateCode(evaluatorPath, tempCodePath);
                        modifyEventCount = 0;
                    }

                }
            }
            key.reset();
        }

        watchService.close();
    }

    private static void evaluateCode(Path evaluatorPath, Path tempCodePath) throws IOException, InterruptedException {
        long start = System.nanoTime();
        final String evaluationCmd = String.format("\"%s\" \"%s\"",
                evaluatorPath.toString(),
                tempCodePath.toString());

        StdoutCommandRunner.runCommandToStdout(evaluationCmd);
        long end = System.nanoTime();
        long elapsedMs = (end - start) / 1_000_000;
        System.out.println("Ran in " + elapsedMs + "ms");
        /* reset count for subsequent changes */
    }
}
