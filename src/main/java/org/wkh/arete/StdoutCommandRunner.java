package org.wkh.arete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StdoutCommandRunner {
    public static Thread commandOutputThread(Process p) {
        return new Thread(() -> {
            String line;
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream(),
                    StandardCharsets.UTF_8));

            try {
                while ((line = input.readLine()) != null)
                    System.out.println(line);

                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static int runCommandToStdout(String cmd) throws IOException, InterruptedException {
        final Process process = Runtime.getRuntime().exec(cmd);
        Thread thread = commandOutputThread(process);

        thread.start();
        int result = process.waitFor();
        thread.join();

        return result;
    }
}
