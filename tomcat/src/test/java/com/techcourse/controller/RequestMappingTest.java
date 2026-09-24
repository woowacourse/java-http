package com.techcourse.controller;

import org.apache.coyote.http11.request.FormContents;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    @Test
    @DisplayName("등록된 경로에 해당하는 컨트롤러를 반환한다.")
    void findMappedController() {
        // given
        Controller loginController = (request, response) -> {
        };
        Controller staticController = (request, response) -> {
        };
        RequestMapping requestMapping = new RequestMapping(
                Map.of("/login", loginController),
                staticController
        );

        // when
        Controller controller = requestMapping.getController(request("GET /login HTTP/1.1"));

        // then
        assertThat(controller).isSameAs(loginController);
    }

    @Test
    @DisplayName("등록되지 않은 경로는 기본 컨트롤러를 반환한다.")
    void findDefaultController() {
        // given
        Controller staticController = (request, response) -> {
        };
        RequestMapping requestMapping = new RequestMapping(Map.of(), staticController);

        // when
        Controller controller = requestMapping.getController(request("GET /index.html HTTP/1.1"));

        // then
        assertThat(controller).isSameAs(staticController);
    }

    private HttpRequest request(String requestLine) {
        return new HttpRequest(
                new RequestLine(requestLine),
                HttpHeaders.empty(),
                FormContents.from(""),
                org.apache.coyote.http11.request.Cookies.from(null)
        );
    }
}
