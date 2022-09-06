package org.apache.coyote.model;

import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.request.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    @DisplayName("확장자가 없는 경우 html을 추가한다.")
    void checkNotExtensionAddDefaultExtension() {
        // given
        String requestLine = "GET /index HTTP/1.1";
        String expected = "/index.html";
        final InputStream inputStream = new ByteArrayInputStream(requestLine.getBytes());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest actual = HttpRequest.of(reader);

        // then
        assertThat(actual.getPath()).isEqualTo(expected);
    }

    @Test
    @DisplayName("param이 없는 경우 빈 값이 들어간다.")
    void checkNotParam() {
        // given
        String requestLine = "GET /index HTTP/1.1";
        final InputStream inputStream = new ByteArrayInputStream(requestLine.getBytes());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest actual = HttpRequest.of(reader);

        // then
        assertThat(actual.getParams()
                .isEmpty()).isTrue();
    }

    @Test
    @DisplayName("HttpMethod를 검증한다.")
    void checkHttpMethod() {
        // given
        String requestLine = "POST /index HTTP/1.1";
        final InputStream inputStream = new ByteArrayInputStream(requestLine.getBytes());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        HttpRequest actual = HttpRequest.of(reader);

        // then
        assertThat(actual.getHttpMethod()).isEqualTo(Method.POST);
    }
}
