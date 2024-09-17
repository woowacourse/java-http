package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    void maxThreads_동시_요청_테스트() throws InterruptedException {
        // given
        int maxThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
        Connector connector = new Connector(8080, 100, maxThreads);

        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        // when
        connector.start();

        IntStream.range(0, maxThreads)
                .forEach(count -> executorService.submit(() -> {
                    try {
                        Socket socket = new Socket("localhost", 8080);
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(httpRequest.getBytes());
                        outputStream.flush();

                        Thread.sleep(2000);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));

        executorService.shutdown();
        executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);

        // then
        assertThat(executorService.isTerminated()).isTrue();
        connector.stop();
    }

    @Test
    void acceptCount_대기열_테스트() throws InterruptedException {
        // given
        int acceptCount = 11;
        ExecutorService executorService = Executors.newFixedThreadPool(acceptCount);
        Connector connector = new Connector(808, acceptCount, 1);

        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        // when
        connector.start();

        IntStream.range(0, acceptCount)
                .forEach(count -> executorService.submit(() -> {
                    try {
                        Socket socket = new Socket("localhost", 8080);
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(httpRequest.getBytes());
                        outputStream.flush();

                        Thread.sleep(2000);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));

        executorService.shutdown();
        executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);

        // then
        assertThat(executorService.isTerminated()).isTrue();
        connector.stop();
    }
}
