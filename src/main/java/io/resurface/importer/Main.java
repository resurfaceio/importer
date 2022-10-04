// © 2016-2022 Resurface Labs Inc.

package io.resurface.importer;

import io.resurface.messages.MessageFileReader;

import java.io.*;
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
     * Reads the input file and sends each line as a logger message.
     */
    public Main() throws Exception {
        // read location of input file
        String file = System.getProperty("FILE");
        if (file == null) throw new IllegalArgumentException("Missing FILE");
        System.out.println("FILE=" + file);

        // calculate destination url if not provided
        String url = System.getProperty("URL");
        if (url == null) {
            String host = System.getProperty("HOST");
            if (host == null) host = "localhost";
            System.out.println("HOST=" + host);
            String port = System.getProperty("PORT");
            if (port == null) port = "7701";
            System.out.println("PORT=" + port);
            if (port.equals("80") || port.equals("443")) {
                url = (port.equals("443") ? "https://" : "http://") + host + "/fluke/message";
            } else {
                url = "http://" + host + ":" + port + "/message";
            }
        }
        System.out.println("URL=" + url);
        parsed_url = new URL(url);

        // read repeat & saturation_stop options
        String repeat = System.getProperty("REPEAT");
        if (repeat == null) repeat = "yes";
        System.out.println("REPEAT=" + repeat);
        if ("yes".equalsIgnoreCase(repeat)) {
            String saturated_stop = System.getProperty("SATURATED_STOP");
            if (saturated_stop == null) saturated_stop = "no";
            System.out.println("SATURATED_STOP=" + saturated_stop);
            if ("yes".equalsIgnoreCase(saturated_stop)) {
                saturated_url = new URL(url.substring(0, url.lastIndexOf("/")) + "/saturated");
                System.out.println("SATURATED_URL=" + saturated_url);
            }
        }

        // create thread to send batches asynchronously
        new Thread(new BatchSender()).start();

        // read file lines and submit messages in batches
        do {
            try (MessageFileReader reader = new MessageFileReader(file)) {
                reader.iterate((line) -> {
                    try {
                        this.batch.add(line);
                        if (this.batch.size() == BATCH_SIZE) submit_current_batch();
                    } catch (RuntimeException | InterruptedException re) {
                        // do nothing
                    }
                });
            }
            submit_current_batch();  // flush lingering messages
        } while ("yes".equalsIgnoreCase(repeat));

        // signal sender thread to stop via poison
        batch_queue.put(POISON_BATCH);
    }

    /**
     * Submits the current batch to be sent and creates a new empty batch.
     */
    private void submit_current_batch() throws InterruptedException {
        batch_queue.put(this.batch);
        this.batch = new ArrayList<>();
    }

    /**
     * Worker thread that sends batches of messages.
     */
    class BatchSender implements Runnable {

        public void run() {
            try {
                while (true) {
                    List<String> b = batch_queue.take();

                    // exit if poisoned
                    if (b == POISON_BATCH) System.exit(0);

                    // exit if database is saturated
                    if (saturated_url != null) {
                        HttpURLConnection c = (HttpURLConnection) saturated_url.openConnection();
                        c.setConnectTimeout(5000);
                        c.setReadTimeout(5000);
                        c.setRequestMethod("GET");
                        boolean saturated;
                        try (InputStream is = c.getInputStream()) {
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                                saturated = "true".equals(br.readLine());
                            }
                        }
                        if (saturated) System.exit(0);
                    }

                    // make request to database
                    HttpURLConnection c = (HttpURLConnection) parsed_url.openConnection();
                    c.setConnectTimeout(5000);
                    c.setReadTimeout(5000);
                    c.setRequestMethod("POST");
                    c.setRequestProperty("Content-Encoding", "deflated");
                    c.setRequestProperty("Content-Type", "application/ndjson; charset=UTF-8");
                    c.setRequestProperty("User-Agent", "Resurface/v3.3.x (importer)");
                    c.setDoOutput(true);
                    try (OutputStream os = c.getOutputStream()) {
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
                    int response_code = c.getResponseCode();
                    if (response_code != 204) {
                        System.out.println("Failed with response code: " + response_code);
                        System.exit(-1);
                    }

                    // update running state
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
    private URL saturated_url;
    private final long started = System.currentTimeMillis();

    private static final List<String> POISON_BATCH = new ArrayList<>();

}