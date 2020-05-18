package twitter;

import com.twitter.hbc.httpclient.BasicClient;
import twitter.files.FileUtils;
import twitter.tweeutils.ClientSupplier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TweeterReader {

    private static final String CONSUMER_KEY = "GPArF3hn3QxTf9v6GrwAczcGO";
    private static final String CONSUMER_SECRET = "7dUNMoPKPWtQZ7TcuhoqWTkuTpkSgRqSGk9vPBfXWPMK8NMXxn";
    private static final String TOKEN = "1908368161-IXatUs3pv25qo6C8gy2Rl7LPWzIz9PsCo9cG45l";
    private static final String SECRET = "S20EJhj1db8MIF5xpv5P9UxvCjBTA5A8JTtreeEZNLKei";

    private final AtomicInteger counter = new AtomicInteger(0);

    public void run() throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);

        BasicClient client = ClientSupplier.prepareAndConnectDefaultClient(CONSUMER_KEY,
                CONSUMER_SECRET,
                TOKEN,
                SECRET,
                queue);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        final int FILES_COUNT = 50;
        CountDownLatch latch = new CountDownLatch(FILES_COUNT);
        for (int i = 0; i < FILES_COUNT; i++) {
            executor.execute(() -> {
                File resultFile = FileUtils.createFile("C:\\temp\\result-" + counter.addAndGet(1) + ".txt");
                if (resultFile.canWrite()) {
                    try (OutputStream outputStream = new FileOutputStream(resultFile);
                         OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {

                        int rowCount = 100;
                        while (!client.isDone() && rowCount-- > 0) {
                            writer.write(queue.take());
                        }
                        writer.flush();

                        latch.countDown();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        latch.await();
        executor.shutdown();

        client.stop();
    }

    public static void main(String[] args) throws Exception {
        TweeterReader reader = new TweeterReader();
        reader.run();
    }

}
