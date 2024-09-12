package com.techcourse.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.HttpRequestConvertor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnauthorizedInterceptorTest {

    @DisplayName("해당 path가 Unauthorized인 Path이면 true를 반환한다")
    @Test
    void isInterceptPath() throws IOException {
        final String interceptPath = String.join("\r\n",
                "GET /401 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        InputStream inputStream = new ByteArrayInputStream(interceptPath.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestConvertor.convertHttpRequest(bufferedReader);

        UnauthorizedInterceptor unauthorizedInterceptor = new UnauthorizedInterceptor();

        assertThat(unauthorizedInterceptor.isInterceptPath(httpRequest)).isTrue();
    }

    @DisplayName("해당 path가 Unauthorized인 Path가 아니면 false를 반환한다")
    @Test
    void isNotInterceptPath() throws IOException {
        final String notInterceptPath = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        InputStream inputStream = new ByteArrayInputStream(notInterceptPath.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestConvertor.convertHttpRequest(bufferedReader);

        UnauthorizedInterceptor unauthorizedInterceptor = new UnauthorizedInterceptor();

        assertThat(unauthorizedInterceptor.isInterceptPath(httpRequest)).isFalse();
    }
}
