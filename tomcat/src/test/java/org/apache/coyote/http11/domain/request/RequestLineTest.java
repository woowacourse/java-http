package org.apache.coyote.http11.domain.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.domain.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RequestLineTest {

    @Test
    @DisplayName("RequestLine 를 생성한다.")
    void createRequestLine() {
        String line = "GET /index.html HTTP/1.1";

        RequestLine requestLine = new RequestLine(line);

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getRequestURI()).isEqualTo("/index.html");
        assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
    }

    @ParameterizedTest
    @CsvSource({
            "'GET /index.html', '2'",
            "'POST /index.html HTTP/1.1 EXTRA', '4'"
    })
    @DisplayName("RequestLine 을 생성할 때 길이가 잘못된 경우 예외를 던진다.")
    void createRequestLineWithInvalidLength(String invalidLine, int invalidLength) {

        assertThatThrownBy(() -> new RequestLine(invalidLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Request Line Length: " + invalidLength);
    }

    @Test
    @DisplayName("RequestLine 을 생성할 때 Method 가 잘못된 경우 예외를 던진다.")
    void createRequestLineWithInvalidMethod() {
        String line = "INVALID /index.html HTTP/1.1";

        assertThatThrownBy(() -> new RequestLine(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid HTTP method: INVALID");
    }
}
