package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    @DisplayName("요청 메서드가 GET 이면 doGet 메서드를 실행한다.")
    void doGet() {
        // given
        HttpRequest request = HttpRequest.parse(List.of("GET / HTTP/1.1"));
        AtomicBoolean executed = new AtomicBoolean(false);
        Controller controller = new AbstractController() {
            @Override
            protected void doGet(HttpRequest request, Builder responseBuilder) {
                executed.set(true);
            }
        };

        // when
        controller.service(request, null);

        // then
        assertTrue(executed.get());
    }

    @Test
    @DisplayName("요청 메서드가 POST 이면 doPost 메서드를 실행한다.")
    void doPost() {
        // given
        HttpRequest request = HttpRequest.parse(List.of("POST / HTTP/1.1"));
        AtomicBoolean executed = new AtomicBoolean(false);
        Controller controller = new AbstractController() {
            @Override
            protected void doPost(HttpRequest request, Builder responseBuilder) {
                executed.set(true);
            }
        };

        // when
        controller.service(request, null);

        // then
        assertTrue(executed.get());
    }
}
