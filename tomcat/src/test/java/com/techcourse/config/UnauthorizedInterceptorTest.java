package com.techcourse.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.httprequest.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestMaker;

class UnauthorizedInterceptorTest {

    @DisplayName("해당 path가 Unauthorized인 Path이면 true를 반환한다")
    @Test
    void isInterceptPath() {
        final String interceptPath = String.join("\r\n",
                "GET /401 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(interceptPath);

        UnauthorizedInterceptor unauthorizedInterceptor = new UnauthorizedInterceptor();

        assertThat(unauthorizedInterceptor.isInterceptPath(httpRequest)).isTrue();
    }

    @DisplayName("해당 path가 Unauthorized인 Path가 아니면 false를 반환한다")
    @Test
    void isNotInterceptPath() {
        final String notInterceptPath = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(notInterceptPath);

        UnauthorizedInterceptor unauthorizedInterceptor = new UnauthorizedInterceptor();

        assertThat(unauthorizedInterceptor.isInterceptPath(httpRequest)).isFalse();
    }
}
