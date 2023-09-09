package org.apache.catalina.connector;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.coyote.RunnableProcessor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.HttpClient;

class ConnectorTest {


    private static final Logger logger = LoggerFactory.getLogger(ConnectorTest.class);

    @Test
    void max_threads_검증() throws Exception {
        int requestsCount = 5;
        int acceptCount = 0;
        int maxThreadsCount = 2;
        CountDownLatch latch = new CountDownLatch(requestsCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Connector connector = getConnector(acceptCount, maxThreadsCount, 600);
        for (int i = 0; i < requestsCount; i++) {
            new Thread(() -> {
                try {
                    HttpClient.send(connector.getLocalPort(),  "", 1000);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        assertAll(
            () -> assertEquals(maxThreadsCount, successCount.get()),
            () -> assertEquals(requestsCount - maxThreadsCount, failCount.get())
        );

        connector.stop();
    }

    private static Connector getConnector(int acceptCount, int maxThreadsCount, int sleepTime) {
        Connector connector = new Connector(8080, acceptCount, maxThreadsCount,
            socket -> new RunnableProcessor() {
                @Override
                public void process(Socket socket) {
                }

                @Override
                public void run() {
                    try {
                        Thread.sleep(sleepTime);
                        OutputStream outputStream = socket.getOutputStream();

                        outputStream.write(String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Length: 12",
                            "Content-Type: text/html;charset=utf-8",
                            "",
                            "Hello world!").getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    } catch (InterruptedException | IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            });

        connector.start();
        return connector;
    }
}