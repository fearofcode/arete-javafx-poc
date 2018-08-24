package org.wkh.arete;

import java.io.*;
import java.util.*;

public class EvaluationClient {
    public static final String DONE = "--done--";
    public static final byte[] WARMUP = "warmup\n".getBytes();

    private final OutputStream stdin;
    private final BufferedReader reader;

    public EvaluationClient() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("application.properties"));
        final String pythonPath = properties.getProperty("pythonPath");

        final String path = "evaluator_server.py";

        final ProcessBuilder builder = new ProcessBuilder(pythonPath, path);
        final Process process = builder.start();
        stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stdout));
        doWarmup();
    }

    public String evaluatePath(byte[] path) throws IOException {
        stdin.write(path);

        stdin.flush();

        String line;

        ArrayList<String> lines = new ArrayList<>();

        do {
            line = reader.readLine ();

            if (line.startsWith(DONE)) {
                break;
            }

            lines.add(line);
        } while (true);

        return String.join("\n", lines);
    }

    private void doWarmup() throws IOException {
        stdin.write(WARMUP);
        stdin.flush();
        reader.readLine();
    }

    public static void main(String[] args) throws IOException {
        final EvaluationClient client = new EvaluationClient();
        final Scanner scanner = new Scanner(System.in);
        final byte[] path = (scanner.nextLine() + "\n").getBytes();
        long start = System.nanoTime();
        System.out.println(client.evaluatePath(path));
        long end = System.nanoTime();

        System.out.println((end - start) / 1_000_000.0);
    }

}
