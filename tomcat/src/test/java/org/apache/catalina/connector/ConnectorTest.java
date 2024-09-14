package org.apache.catalina.connector;

import org.apache.catalina.Manager;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestProcessor;

import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConnectorTest {

    private static final int MAX_CONNECTIONS = 150;
    private static final int MAX_THREADS = 100;
    public static final int WAIT_EXPECTED = 50;

    @Test
    @DisplayName("최대 Thread Pool의 크기가 100, 모든 Thread가 사용 중인(Busy) 상태이면 50명까지 대기 상태로 만든다.")
    void maxConnectionTest() throws Exception {
        // 테스트를 종료할때 사용한다.
        CountDownLatch testEndLatch = new CountDownLatch(1);

        // 테스트가 종료될때까지 대기한다.
        Http11Processor http11Processor = new TestProcessor(
                null,
                null,
                testEndLatch
        );

        CoyoteProcessorFactory mockFactory = mock(CoyoteProcessorFactory.class);
        when(mockFactory.getHttp11Processor(any(), any()))
                .thenReturn(http11Processor);

        Connector connector = new Connector(
                8080,
                100,
                MAX_CONNECTIONS,
                MAX_THREADS,
                mock(Manager.class),
                mockFactory
        );
        connector.start();

        // maxConnections + 100번 요청한다.
        setUpConnection(testEndLatch);

        // maxConnections + 100번 요청을 보내도, 활성 스레드 수는 max thread 수와 일치한다.
        assertThat(connector.getActiveConnect()).isEqualTo(MAX_THREADS);

        // 대기 큐의 사이즈는 최대 maxConnections - maxThreads이다.
        assertThat(connector.getWaitConnect()).isEqualTo(WAIT_EXPECTED);

        // connector.process 내부 getHttp11Processor의 호출 수는 connector의 accept 수이다.
        // maxConnections + 100번 요청해도 accept는 max_connections만큼 한다.
        verify(mockFactory, times(MAX_CONNECTIONS)).getHttp11Processor(any(), any());

        testEndLatch.countDown();
        connector.stop();
    }

    private void setUpConnection(CountDownLatch testEndLatch) throws Exception {
        int connectionCount = MAX_CONNECTIONS + 100;
        ExecutorService executorService = Executors.newFixedThreadPool(connectionCount);
        CountDownLatch taskLatch = new CountDownLatch(connectionCount);

        for (int i = 0; i < connectionCount; i++) {
            Thread.sleep(50);
            executorService.submit(() -> {
                try {
                    send(testEndLatch, taskLatch);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        taskLatch.await();
    }

    private void send(CountDownLatch testEndLatch, CountDownLatch taskLatch) throws Exception {
        taskLatch.countDown();
        try (Socket socket = new Socket("localhost", 8080)) {
            testEndLatch.await();
        }
    }
}
