package org.apache.coyote.http11.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.exception.InvalidHttpRequestStartLineException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Collections;
import java.util.List;

class HttpRequestTest {

    @DisplayName("HTTP 요청을 생성한다.")
    @Test
    void of() {
        // given
        final String startLine = "GET /index.html HTTP/1.1";
        final List<String> lines = List.of("Host: localhost:8080", "Connection: keep-alive", "Accept: */*");

        // when
        final HttpRequest httpRequest = HttpRequest.of(startLine, lines);

        // then
        assertAll(
                () -> assertThat(httpRequest.getUri()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getAcceptType()).isEqualTo("*/*")
        );
    }

    @DisplayName("HTTP 요청에 method가 존재하지 않는 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "NOTHING", "GGG"})
    void of_throwsException_ifInvalidHttpRequest(final String startLine) {
        // given
        final List<String> lines = Collections.emptyList();

        // when, then
        assertThatThrownBy(() -> HttpRequest.of(startLine, lines))
                .isInstanceOf(InvalidHttpRequestStartLineException.class);
    }

    @DisplayName("HTTP 요청 시, URI가 제대로 파싱되는지 확인한다.")
    @Test
    void getUri() {
        // given
        final String startLine = "GET /index.html HTTP/1.1";
        final String expected = "/index.html";
        final HttpRequest httpRequest = HttpRequest.of(startLine, Collections.emptyList());

        // when
        final String actual = httpRequest.getUri();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("HTTP 요청 시, AcceptType이 제대로 파싱되는지 확인한다.")
    @Test
    void getAcceptType() {
        // given
        final String expected = "text/html";
        final String startLine = "GET /index.html HTTP/1.1";
        final List<String> lines = List.of("Host: localhost:8080", "Connection: keep-alive", "Accept: text/html");
        final HttpRequest httpRequest = HttpRequest.of(startLine, lines);

        // when
        final String actual = httpRequest.getAcceptType();


        // then
        assertThat(actual).isEqualTo(expected);
    }
}
