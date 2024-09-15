package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.RequestMappings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class ConnectorTest {

    private static volatile boolean sendResponse = false;

    @Test
    @DisplayName("350번째 연결 요청은 성공하고 351번째 요청은 실패한다.")
    void requestFailWhenTooMany() throws InterruptedException {
        var connector = makeTestConnector();
        connector.start();
        Thread.sleep(1000);
        ExecutorService executorService = Executors.newFixedThreadPool(349);
        List<Future<HttpResponse<String>>> submits = new ArrayList<>();
        for (int i = 0; i < 351; i++) {
            submits.add(executorService.submit(() -> TestHttpUtils.send("/", 400000)));
            if (i == 350) {
                sendResponse = true;
            }
        }
        Thread.sleep(4000);
        Future<HttpResponse<String>> beforeLast = submits.get(349);
        Future<HttpResponse<String>> last = submits.getLast();
        assertAll(
                () -> assertThat(beforeLast.isDone()).isTrue(),
                () -> assertThatThrownBy(last::exceptionNow)
        );
    }

    private Connector makeTestConnector() {
        RequestMapping slow = getSlowRequestMapping();
        RequestMappings requestMappings = new RequestMappings(slow);
        return new Connector(requestMappings);
    }

    private RequestMapping getSlowRequestMapping() {
        return new RequestMapping((request, response) -> {
            while (!sendResponse) {
                Thread.onSpinWait();
            }
            response.setRedirect("/index.html");
        }, "/");
    }
}
