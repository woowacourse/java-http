package org.apache.catalina.connector;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.MyContext;

class ConnectorTest {

    private Connector connector;

    @BeforeEach
    void setUp() {
        connector = new Connector(new MyContext());
        connector.start();
    }

    @Test
    @DisplayName("동시 요청이 최대 쓰레드 이하일 때 모든 요청 처리 성공")
    void testConcurrentRequestsBelowMaxThreads() throws InterruptedException {
        int concurrentRequests = 200;
        sendRequests(concurrentRequests);

        assertAll(
                () -> assertEquals(0, connector.getPendingRequestCount()),
                () -> assertEquals(concurrentRequests, connector.getCompletedRequestCount())
        );
    }


    void sendRequests(int currentRequests) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(currentRequests);
        CountDownLatch latch = new CountDownLatch(currentRequests);

        for (int i = 0; i < currentRequests; i++) {
            executor.submit(() -> {
                sendRequest();
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertTrue(connector.getCompletedRequestCount() > 0);
        assertEquals(0, connector.getActiveRequestCount());
        assertTrue(connector.getPendingRequestCount() <= currentRequests);
    }

    private void sendRequest() {
        try (Socket socket = new Socket("localhost", 8080)) {
            socket.getOutputStream().write("GET / HTTP/1.1\r\n\r\n".getBytes());
            socket.getOutputStream().flush();
            socket.getInputStream().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
