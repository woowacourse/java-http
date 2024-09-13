package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.RequestMappings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class ConnectorTest {

    @Test
    @DisplayName("350번째 연결 요청은 성공하고 351번째 요청은 실패한다.")
    void requestFailWhenTooMany() throws InterruptedException {
        var connector = makeSlowConnector();
        connector.start();
        Thread.sleep(1000);
        ExecutorService executorService = Executors.newFixedThreadPool(349);
        for (int i = 0; i < 349; i++) {
            executorService.submit(() -> TestHttpUtils.send("/", 400000));
        }
        assertAll(
                () -> assertDoesNotThrow(() -> TestHttpUtils.send("/fast", 3)),
                () -> assertThatThrownBy(() -> TestHttpUtils.send("/fast", 3))
        );
    }

    private Connector makeSlowConnector() {
        RequestMapping slow = getSlowRequestMapping();
        RequestMapping fast = getFastRequestMapping();
        RequestMappings requestMappings = new RequestMappings(slow, fast);
        return new Connector(requestMappings, 250);
    }

    private RequestMapping getSlowRequestMapping() {
        return new RequestMapping((request, response) -> {
            try {
                Thread.sleep(10000000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            response.setRedirect("/index.html");
        }, "/");
    }

    private RequestMapping getFastRequestMapping() {
        return new RequestMapping((request, response) -> {
            response.setRedirect("/index.html");
        }, "/fast");
    }
}
