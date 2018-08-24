package org.wkh.arete;

import java.io.*;
import java.util.Properties;

public class SimpleClient {
    public static final String DONE = "--done--";
    public static final byte[] WARMUP = "warmup\n".getBytes();

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("application.properties"));
        final String pythonPath = properties.getProperty("pythonPath");

        if (pythonPath == null) {
            System.err.println("Set pythonPath property in application.properties and try again");
            return;
        }

        final String path = "simple_server.py";

        final ProcessBuilder builder = new ProcessBuilder(pythonPath, path);
        final Process process = builder.start();
        final OutputStream stdin = process.getOutputStream();
        final InputStream stdout = process.getInputStream();

        doWarmup(stdin, stdout);

        long start = System.nanoTime();

        stdin.write("/Users/Warren/Downloads/evaluation_test.py\n".getBytes());
        stdin.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        String line;

        do {
            line = reader.readLine ();

            if (line != null) {
                if (!line.startsWith(DONE)) {
                    System.out.println(line);
                } else {
                    break;
                }
            }
        } while (true);

        long end = System.nanoTime();

        System.out.println((end - start) / 1_000_000.0);
    }

    private static void doWarmup(OutputStream stdin, InputStream stdout) throws IOException {
        stdin.write(WARMUP);
        stdin.flush();
        new BufferedReader(new InputStreamReader(stdout)).readLine();
    }
}
