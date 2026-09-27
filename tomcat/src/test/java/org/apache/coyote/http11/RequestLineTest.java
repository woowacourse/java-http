package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RequestLineTest {

    @ParameterizedTest
    @CsvSource({
        "/index, /index",
        "/index.html, /index",
        "/login, /login",
        "/login.html, /login",
        "/register, /register",
        "/register.html, /register"
    })
    void 별칭_경로를_동일한_요청_경로로_정규화한다(
        final String requestPath,
        final String expectedPath
    ) {
        // given & when
        final RequestLine requestLine = RequestLine.from(
            "GET " + requestPath + " HTTP/1.1");

        // then
        assertThat(requestLine.path()).isEqualTo(expectedPath);
    }

    @ParameterizedTest
    @CsvSource({
        "'GET   /index.html   HTTP/1.1', /index",
        "'GET\t/login.html\tHTTP/1.1', /login"
    })
    void 여러_공백으로_구분된_요청_줄을_파싱한다(
        final String rawRequestLine,
        final String expectedPath
    ) {
        final RequestLine requestLine = RequestLine.from(rawRequestLine);

        assertThat(requestLine.path()).isEqualTo(expectedPath);
    }
}
