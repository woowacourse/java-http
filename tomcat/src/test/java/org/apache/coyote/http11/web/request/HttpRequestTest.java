package org.apache.coyote.http11.web.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.support.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.*;
import java.util.Map;

class HttpRequestTest {

    @DisplayName("HTTP 요청을 생성한다.")
    @Test
    void create() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive", "Accept: */*");
        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        final HttpRequest httpRequest = HttpRequestParser.execute(bufferedReader);

        // then
        assertAll(
                () -> assertThat(httpRequest.getUri()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getHeaders().getValues()).containsValue("*/*")
        );
    }

    @DisplayName("HTTP 요청에 method가 존재하지 않는 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "NOTHING", "GGG"})
    void create_throwsException_ifInvalidHttpRequest(final String startLine) {
        // given
        final String rawRequest = String.join("\r\n", startLine);
        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when, then
        assertThatThrownBy(() -> HttpRequestParser.execute(bufferedReader))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @DisplayName("HTTP 요청 시, URI가 제대로 파싱되는지 확인한다.")
    @Test
    void getUri() throws IOException {
        // given
        final String rawRequest = String.join("\r\n", "GET /index.html HTTP/1.1");
        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        final HttpRequest httpRequest = HttpRequestParser.execute(bufferedReader);
        final String actual = httpRequest.getUri();
        final String expected = "/index.html";

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("HTTP 요청 시, AcceptType이 제대로 파싱되는지 확인한다.")
    @Test
    void getAcceptType() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive", "Accept: text/html");
        final InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        final HttpRequest httpRequest = HttpRequestParser.execute(bufferedReader);
        final Map<HttpHeader, String> headers = httpRequest.getHeaders().getValues();
        final String expected = "text/html";

        // then
        assertThat(headers).containsValue(expected);
    }
}
