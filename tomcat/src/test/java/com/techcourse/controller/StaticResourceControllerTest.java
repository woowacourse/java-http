package com.techcourse.controller;

import org.apache.coyote.http11.request.Cookies;
import org.apache.coyote.http11.request.FormContents;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    @Test
    @DisplayName("GET 요청의 정적 리소스를 응답한다.")
    void sendStaticResource() throws Exception {
        // given
        HttpRequest request = new HttpRequest(
                new RequestLine("GET /index.html HTTP/1.1"),
                HttpHeaders.empty(),
                FormContents.from(""),
                Cookies.from(null)
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        new StaticResourceController().service(request, new HttpResponse(outputStream));

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/html;charset=utf-8");
    }
}
