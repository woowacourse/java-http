package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.techcourse.handler.FrontController;
import com.techcourse.handler.HandlerMapping;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ConnectorTest {

    private final HandlerMapping handlerMapping = new HandlerMapping(Map.of(
            "/", new SlowHandler()
    ));
    private final FrontController controller = new FrontController(handlerMapping);
    private Connector connector;

    @AfterEach
    void tearDown() {
        connector.stop();
    }

    @DisplayName("maxThreads만큼 요청 시 Thread Pool Size는 maxThreads이며 Queue Size는 0이다.")
    @Test
    void maxThreads() throws InterruptedException {
        connector = new Connector(controller, 1234, 1, 2);
        connector.start();

        int numberOfRequest = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequest);

        for (int i = 0; i < numberOfRequest; i++) {
            executorService.execute(() -> TestHttpUtils.send("/"));
        }

        Thread.sleep(1000);

        assertAll(
                () -> assertThat(connector.getPoolSize()).isEqualTo(2),
                () -> assertThat(connector.getQueueSize()).isZero()
        );
    }

    @DisplayName("maxThreads보다 많은 요청 시 Thread Pool Size는 maxThreads이며 Queue Size는 요청 수에서 maxThreads를 뺀 값이다.")
    @Test
    void acceptCount() throws InterruptedException {
        connector = new Connector(controller, 1234, 2, 3);
        connector.start();

        int numberOfRequest = 6;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequest);

        for (int i = 0; i < numberOfRequest; i++) {
            executorService.execute(() -> {
                TestHttpUtils.send("/");
            });
        }

        Thread.sleep(1000);

        assertAll(
                () -> assertThat(connector.getPoolSize()).isEqualTo(3),
                () -> assertThat(connector.getQueueSize()).isEqualTo(2)
        );
    }

    static class SlowHandler extends AbstractController {

        @Override
        protected void doGet(HttpRequest request, HttpResponse response) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
