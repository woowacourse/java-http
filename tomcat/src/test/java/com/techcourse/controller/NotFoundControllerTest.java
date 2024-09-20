package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestFixtures.*;

class NotFoundControllerTest {

    private final NotFoundController notFoundController = new NotFoundController();

    private void handleRequest(HttpRequest httpRequest, String[] expectedResponses) throws Exception {
        HttpResponse httpResponse = new HttpResponse();

        notFoundController.service(httpRequest, httpResponse);
        for (String expectedResponse : expectedResponses) {
            assertThat(new String(ResponseFormatter.toResponseFormat(httpResponse), StandardCharsets.UTF_8)).contains(expectedResponse);
        }
    }

    @Test
    @DisplayName("존재하지 않는 주소에 접근하면 /static/404.html 페이지로 리다이렉트한다.")
    void notFoundPage() throws Exception {
        // given
        final HttpRequest httpRequest = buildHttpRequest(GET, "/fake", "");

        // when, then
        handleRequest(httpRequest, new String[]{HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /404.html"});
    }

    @Test
    @DisplayName("존재하지 않는 정적 파일 주소에 접근하면 /static/404.html 페이지로 리다이렉트한다.")
    void notFoundPage_staticIndex() throws Exception {
        // given
        final HttpRequest httpRequest = buildHttpRequest(GET, "/fake.html", "");

        // when, then
        handleRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /404.html"});
    }
}
