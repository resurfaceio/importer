// © 2016-2022 Resurface Labs Inc.

package io.resurface.importer;

import io.resurface.messages.MessageFileReader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.DeflaterOutputStream;

/**
 * Imports data to Resurface database.
 */
public class Main {

    /**
     * Number of messages written at once.
     */
    public static final int BATCH_SIZE = 32;

    /**
     * Runs importer as command-line program.
     */
    public static void main(String[] args) throws Exception {
        new Main();
    }

    /**
     * Reads the target file and sends each line as a logger message.
     */
    public Main() throws Exception {
        // read configuration
        String file = System.getProperty("FILE");
        if (file == null) throw new IllegalArgumentException("Missing FILE");
        System.out.println("FILE=" + file);
        String url = System.getProperty("URL");
        if (url == null) {
            String host = System.getProperty("HOST");
            if (host == null) host = "localhost";
            System.out.println("HOST=" + host);
            String port = System.getProperty("PORT");
            if (port == null) port = "7701";
            System.out.println("PORT=" + port);
            // calculate url
            url = "http://" + host + ":" + port + "/message";
        }
        System.out.println("URL=" + url);
        parsed_url = new URL(url);
        
        String repeat = System.getProperty("REPEAT");
        if (repeat == null) repeat = "yes";
        System.out.println("REPEAT=" + repeat);

        // send all lines in batches
        new Thread(new BatchSender()).start();

        // send messages in a loop until terminated
        boolean keep_going = true;
        while (keep_going) {
            try (MessageFileReader reader = new MessageFileReader(file)) {
                reader.iterate((line) -> {
                    try {
                        this.batch.add(line);
                        if (this.batch.size() == BATCH_SIZE) {
                            ArrayList<String> current_batch = this.batch;
                            this.batch = new ArrayList<>();
                            batch_queue.put(current_batch);
                        }
                    } catch (RuntimeException | InterruptedException re) {
                        // do nothing
                    }
                });
            }

            // flush last messages
            batch_queue.put(this.batch);

            // exit if necessary
            keep_going = "yes".equalsIgnoreCase(repeat);
        }
    }

    /**
     * Worker thread that sends batches of messages.
     */
    class BatchSender implements Runnable {

        public void run() {
            boolean polling = true;
            try {
                while (polling) {
                    List<String> b = batch_queue.take();

                    // make request to database
                    HttpURLConnection url_connection = (HttpURLConnection) parsed_url.openConnection();
                    url_connection.setConnectTimeout(5000);
                    url_connection.setReadTimeout(5000);
                    url_connection.setRequestMethod("POST");
                    url_connection.setRequestProperty("Content-Encoding", "deflated");
                    url_connection.setRequestProperty("Content-Type", "application/ndjson; charset=UTF-8");
                    url_connection.setRequestProperty("User-Agent", "Resurface/2.x (importer)");
                    url_connection.setDoOutput(true);
                    try (OutputStream os = url_connection.getOutputStream()) {
                        try (DeflaterOutputStream dos = new DeflaterOutputStream(os, true)) {
                            for (String m : b) {
                                dos.write(m.getBytes(StandardCharsets.UTF_8));
                                dos.write("\n".getBytes(StandardCharsets.UTF_8));
                            }
                            dos.finish();
                            dos.flush();
                        }
                        os.flush();
                    }

                    // check response from database
                    int response_code = url_connection.getResponseCode();
                    if (response_code != 204) {
                        System.out.println("Failed with response code: " + response_code);
                    }

                    // update running state
                    polling = (b.size() == BATCH_SIZE);
                    messages_written += b.size();
                    status();
                }
            } catch (RuntimeException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Print status summary.
     */
    private void status() {
        long elapsed = System.currentTimeMillis() - started;
        long rate = (messages_written * 1000 / elapsed);
        System.out.println("Messages: " + messages_written + ", Elapsed time: " + elapsed + " ms, Rate: " + rate + " msg/sec");
    }

    private ArrayList<String> batch = new ArrayList<>();
    private final BlockingQueue<List<String>> batch_queue = new ArrayBlockingQueue<>(128);
    private long messages_written = 0;
    private final URL parsed_url;
    private final long started = System.currentTimeMillis();

}