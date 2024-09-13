package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectorTest {

    private static final Logger log = LoggerFactory.getLogger(ConnectorTest.class);
    private static final AtomicInteger count = new AtomicInteger(0);

    @DisplayName("거의 동시에 300개의 요청이 들어오면?")
    @Test
    void test_concurrentRequest() {
        Connector connector = new Connector();
        connector.start();

        for (int i = 0; i < 300; i++) {
            incrementIfOk(TestHttpUtils.send("/"));
            // log.info("pool size = {}", connector.getExecutorPoolSize());
            // log.info("queue size = {}", connector.getExecutorQueueSize());
        }

        assertThat(connector.getExecutorPoolSize()).isEqualTo(250);
        // assertThat(connector.getExecutorQueueSize()).isEqualTo(50);
        connector.stop();
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
