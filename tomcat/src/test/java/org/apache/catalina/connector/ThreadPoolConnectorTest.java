package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.SlowHttp11Processor;
import support.StubSocket;

class ThreadPoolConnectorTest {

    private static final int PORT = 8080;
    private static final int MAX_THREAD_POOL_COUNT = 250;
    private static final int MAX_ACCEPT_COUNT = 100;
    private static final int MAX_REQUEST_COUNT = MAX_THREAD_POOL_COUNT + MAX_ACCEPT_COUNT;
    private static final int DELAY_PER_REQUEST = 1000;

    @DisplayName("최대 ThreadPool의 크기는 250, 모든 Thread가 사용 중인(Busy) 상태이면 100명까지 대기 상태로 만들려면 어떻게 할까?")
    @Test
    void threadPoolTest() throws InterruptedException {
        Function<Socket, Http11Processor> container =
                (connection) -> new SlowHttp11Processor(connection, new CatalinaAdapter(), DELAY_PER_REQUEST);
        ThreadPoolConnector connector =
                new ThreadPoolConnector(PORT, MAX_ACCEPT_COUNT, MAX_THREAD_POOL_COUNT, container);
        connector.start();

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_REQUEST_COUNT);
        for (int i = 0; i < MAX_REQUEST_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    sendRequest(connector);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        Thread.sleep(100);

        assertThat(connector.getActiveCount()).isEqualTo(250);
        assertThat(connector.getWaitCount()).isEqualTo(100);

        connector.stop();
    }

    void sendRequest(Connector connector) throws Exception {
        StubSocket connection = new StubSocket();
        connector.process(connection);
    }
}
