package org.wkh.arete;

import jep.Jep;
import jep.JepException;

import java.io.IOException;
import java.nio.file.*;

public class JepTest {
    public static void main(String[] args) throws JepException, IOException, InterruptedException {
        Jep jep = new Jep();
        String path = "C:\\Users\\Warren\\test_directory\\jep_test.py";
        jep.runScript(path);

        WatchKey key;
        final WatchService watchService = FileSystems.getDefault().newWatchService();
        final Path codeDirectory = Paths.get("C:\\Users\\Warren\\test_directory\\");
        codeDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Running script");
                jep.runScript(path);
                System.out.println("Ran");
            }
            key.reset();
        }
        jep.close();
    }
}
