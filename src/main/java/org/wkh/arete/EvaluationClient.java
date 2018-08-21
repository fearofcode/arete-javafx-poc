package org.wkh.arete;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class EvaluationClient {
    public static String evaluatePath(String path) throws IOException, InterruptedException {
        byte[] payload = path.getBytes(StandardCharsets.UTF_8);

        try(Socket socket = new Socket("localhost", 50051)) {
            socket.getOutputStream().write(payload);

            byte[] response = socket.getInputStream().readAllBytes();
            return new String(response, StandardCharsets.UTF_8);
        }
    }

    public static void main(String argv[]) throws IOException, InterruptedException {
        final String path = "C:\\Users\\Warren\\AppData\\Local\\Temp\\code_eval1222849660043245737\\arete3624003994965102469.py";
        evaluatePath(path);
    }
}
