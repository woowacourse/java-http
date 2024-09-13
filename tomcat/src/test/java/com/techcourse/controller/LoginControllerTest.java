package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.requestLine.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("로그인 Post요청 시, Set-Cookie에 JSESSIONID가 설정된다.")
    @Test
    void Set_Cookie_header_has_JSESSIONID_When_post_login() {
        // given
        HttpRequest httpRequest = HttpRequest.createEmptyHttpRequest();
        httpRequest.setHttpRequestLine(HttpRequestLine.toHttpRequestLine("POST /login HTTP/1.1"));
        httpRequest.setHttpRequestHeader(
                HttpRequestHeader.toHttpRequestHeader(
                        List.of(
                                "Host: localhost:8080 ",
                                "Connection: keep-alive ",
                                "Content-Type: application/x-www-form-urlencoded",
                                "Content-Length: " + "account=gugu&password=password".length()
                        )
                )
        );
        httpRequest.setHttpRequestBody(
                HttpRequestBody.toHttpRequestBody(
                        "account=gugu&password=password",
                        ContentType.APPLICATION_X_WWW_FORM_URLENCODED
                )
        );

        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        // when
        new LoginController().doPost(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.toHttpForm()).containsPattern("Set-Cookie: JSESSIONID=.+");
    }
}
