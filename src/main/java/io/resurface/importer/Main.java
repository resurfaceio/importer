// © 2016-2020 Resurface Labs Inc.

package io.resurface.importer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

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
        String file = System.getenv("FILE");
        if (file == null) throw new IllegalArgumentException("Missing FILE");
        System.out.println("FILE=" + file);
        String host = System.getenv("HOST");
        if (host == null) host = "localhost";
        System.out.println("HOST=" + host);

        // calculate url
        String url = "http://" + host + ":4001/message";
        System.out.println("URL=" + url);
        parsed_url = new URL(url);

        // create reader to file
        BufferedReader reader;
        if (file.endsWith(".ndjson")) {
            reader = new BufferedReader(new FileReader(file));
        } else if (file.endsWith(".ndjson.gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        } else {
            throw new IllegalArgumentException("File is not .ndjson or .ndjson.gz format");
        }

        // send each line as a message
        String message;
        while ((message = reader.readLine()) != null) send(message);
        status();
    }

    private void send(String message) throws Exception {
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

    private void status() {
        long elapsed = System.currentTimeMillis() - started;
        long mb_written = (bytes_written / (1024 * 1024));
        long rate = (messages_written * 1000 / elapsed);
        System.out.println("Messages: " + messages_written + ", Elapsed time: " + elapsed
                + " ms, MB: " + mb_written + ", Rate: " + rate + " msg/sec");
    }

    private long bytes_written = 0;
    private long messages_written = 0;
    private final URL parsed_url;
    private final long started = System.currentTimeMillis();
}