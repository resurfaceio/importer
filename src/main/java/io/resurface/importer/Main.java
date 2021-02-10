// © 2016-2020 Resurface Labs Inc.

package io.resurface.importer;

import io.resurface.messages.MessageFileReader;

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
     * Runs importer as command-line program.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("\n>>> Importer starting");
        new Main();
        System.out.println(">>> Importer finished!\n");
    }

    /**
     * Reads the target file and sends each line as a logger message.
     */
    public Main() throws Exception {
        // read configuration
        String file = System.getProperty("FILE");
        if (file == null) throw new IllegalArgumentException("Missing FILE");
        System.out.println("FILE=" + file);
        String host = System.getProperty("HOST");
        if (host == null) host = "localhost";
        System.out.println("HOST=" + host);

        // calculate url
        String url = "http://" + host + ":4001/message";
        System.out.println("URL=" + url);
        parsed_url = new URL(url);

        // send all lines
        try (MessageFileReader reader = new MessageFileReader(file)) {
            reader.iterate(this::send);
        }

        status();  // show final status
    }

    /**
     * Send raw message to the target URL.
     */
    private void send(String message) {
        try {
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
                if (messages_written++ % 100 == 0) status();
            } else {
                System.out.println("Failed with response code: " + response_code);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Print status summary.
     */
    private void status() {
        long elapsed = System.currentTimeMillis() - started;
        long rate = (messages_written * 1000 / elapsed);
        System.out.println("Messages: " + messages_written + ", Elapsed time: " + elapsed
                + " ms, Rate: " + rate + " msg/sec");
    }

    private long messages_written = 0;
    private final URL parsed_url;
    private final long started = System.currentTimeMillis();
}