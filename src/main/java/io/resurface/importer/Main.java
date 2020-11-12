// © 2016-2020 Resurface Labs Inc.

package io.resurface.importer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Imports data to Resurface database.
 */
public class Main {

    /**
     * Reads the target file and sends each line as a logger message.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Importer starting...");

        // read configuration
        String file = System.getenv("FILE");
        if (file == null) throw new IllegalArgumentException("Missing FILE");
        System.out.println("FILE=" + file);
        String host = System.getenv("HOST");
        if (host == null) host = "localhost";
        System.out.println("HOST=" + host);

        // calculate url
        String url = "http://" + host + ":4001/message";
        System.out.println("URL=" + url);
        URL parsed_url = new URL(url);

        // send each line as a message
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String message;
            while ((message = br.readLine()) != null) {
                HttpURLConnection url_connection = (HttpURLConnection) parsed_url.openConnection();
                url_connection.setConnectTimeout(5000);
                url_connection.setReadTimeout(1000);
                url_connection.setRequestMethod("POST");
                url_connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                url_connection.setDoOutput(true);
                try (OutputStream os = url_connection.getOutputStream()) {
                    os.write(message.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
                int response_code = url_connection.getResponseCode();
                if (response_code == 204) {
                    bytes_written += message.length();
                    messages_written += 1;
                    if (messages_written % 100 == 0) status();
                } else {
                    System.out.println("Failed with response code: " + response_code);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(42);
        }

        status();
        System.out.println("Importer finished!");
    }

    private static void status() {
        long elapsed = System.currentTimeMillis() - started;
        long mb_written = (bytes_written / (1024 * 1024));
        long rate = (messages_written * 1000 / elapsed);
        System.out.println("Messages: " + messages_written + ", Elapsed time: " + elapsed
                + " ms, MB: " + mb_written + ", Rate: " + rate + " msg/sec");
    }

    private static long bytes_written = 0;
    private static long messages_written = 0;
    private static final long started = System.currentTimeMillis();

}
