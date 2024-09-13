package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.Test;
import org.was.controller.ResponseResult;

class DashboardControllerTest {

    @Test
    void GET_요청할_경우_대시보드_페이지_응답결과_반환() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/", null, "HTTP/1.1");
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), new RequestBody(null));

        DashboardController controller = new DashboardController();

        // when
        ResponseResult result = controller.doGet(request);

        // then
        assertAll(
                () ->  assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.OK),
                () ->  assertThat(result.getPath()).isEqualTo("/index.html")
        );
    }

    @Test
    void POST_요청할_경우_404_응답결과_반환() {
        // given
        RequestLine requestLine = new RequestLine("POST", "/", null, "HTTP/1.1");
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), new RequestBody(null));

        DashboardController controller = new DashboardController();

        // when
        ResponseResult result = controller.doPost(request);

        // then
        assertAll(
                () ->  assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.NOT_FOUND),
                () ->  assertThat(result.getBody()).isEqualTo("/404.html")
        );
    }
}
