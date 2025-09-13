package org.apache.coyote.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConnectorConcurrencyTest {

    private Connector connector;
    private final int TEST_PORT = 8081;

    @BeforeEach
    void setUp() {
        connector = new Connector(TEST_PORT, 100, 1, 2, 200);
        connector.start();
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    void tearDown() {
        connector.stop();
    }

    @Test
    void 동시_요청_1000개_처리_테스트() throws InterruptedException {
        final int numberOfRequests = 1000;
        final CountDownLatch latch = new CountDownLatch(numberOfRequests);
        final AtomicInteger successCount = new AtomicInteger(0);
        final ExecutorService executorService = Executors.newFixedThreadPool(50);

        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(() -> {
                try {
                    if (sendHttpRequest()) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        assertThat(successCount.get()).isGreaterThan((int) (numberOfRequests * 0.9));
    }

    @Test
    void 스레드_풀_크기_제한_테스트() throws InterruptedException {
        final int numberOfRequests = 300;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch finishLatch = new CountDownLatch(numberOfRequests);
        final AtomicInteger concurrentConnections = new AtomicInteger(0);
        final AtomicInteger maxConcurrentConnections = new AtomicInteger(0);
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    
                    int current = concurrentConnections.incrementAndGet();
                    maxConcurrentConnections.updateAndGet(max -> Math.max(max, current));
                    
                    sendHttpRequestWithDelay();
                    
                    concurrentConnections.decrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        assertThat(maxConcurrentConnections.get()).isLessThanOrEqualTo(200);
    }

    @Test
    void 순차적_요청_vs_동시_요청_성능_비교() throws InterruptedException {
        final int numberOfRequests = 100;

        long sequentialTime = measureSequentialRequests(numberOfRequests);
        long concurrentTime = measureConcurrentRequests(numberOfRequests);

        assertThat(concurrentTime).isLessThan(sequentialTime);
    }

    private boolean sendHttpRequest() {
        try (Socket socket = new Socket("localhost", TEST_PORT);
             OutputStream out = socket.getOutputStream()) {
            
            String request = "GET / HTTP/1.1\r\n" +
                           "Host: localhost\r\n" +
                           "Connection: close\r\n" +
                           "\r\n";
            
            out.write(request.getBytes());
            out.flush();
            
            Thread.sleep(10);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void sendHttpRequestWithDelay() {
        try (Socket socket = new Socket("localhost", TEST_PORT);
             OutputStream out = socket.getOutputStream()) {
            
            String request = "GET / HTTP/1.1\r\n" +
                           "Host: localhost\r\n" +
                           "Connection: close\r\n" +
                           "\r\n";
            
            out.write(request.getBytes());
            out.flush();
            
            Thread.sleep(50);
        } catch (Exception e) {
            // 테스트용이므로 예외 무시
        }
    }

    private long measureSequentialRequests(int numberOfRequests) {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfRequests; i++) {
            sendHttpRequest();
        }
        
        return System.currentTimeMillis() - startTime;
    }

    private long measureConcurrentRequests(int numberOfRequests) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(numberOfRequests);
        final ExecutorService executorService = Executors.newFixedThreadPool(20);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(() -> {
                try {
                    sendHttpRequest();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();
        
        return System.currentTimeMillis() - startTime;
    }
}
