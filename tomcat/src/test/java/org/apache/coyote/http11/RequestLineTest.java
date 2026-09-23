package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void parsesRequestLine() {
        RequestLine requestLine = RequestLine.parse("GET /login HTTP/1.1");

        assertThat(requestLine.method()).isEqualTo("GET");
        assertThat(requestLine.requestTarget()).isEqualTo("/login");
        assertThat(requestLine.path()).isEqualTo("/login");
        assertThat(requestLine.httpVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void separatesPathFromQuery() {
        RequestLine requestLine = RequestLine.parse("GET /login?next=index HTTP/1.1");

        assertThat(requestLine.requestTarget()).isEqualTo("/login?next=index");
        assertThat(requestLine.path()).isEqualTo("/login");
    }

    @Test
    void extractsRawQueryAfterFirstQuestionMark() {
        RequestLine requestLine = RequestLine.parse(
                "GET /login?next=a?b&name=%EA%B0%80 HTTP/1.1");

        assertThat(requestLine.path()).isEqualTo("/login");
        assertThat(requestLine.query()).isEqualTo("next=a?b&name=%EA%B0%80");
    }

    @Test
    void returnsEmptyQueryWhenMissingOrEmpty() {
        for (String target : List.of("/login", "/login?")) {
            RequestLine requestLine = RequestLine.parse("GET " + target + " HTTP/1.1");

            assertThat(requestLine.query()).isEmpty();
        }
    }

    @Test
    void preservesTrailingSpaceSupport() {
        RequestLine requestLine = RequestLine.parse("POST /login HTTP/1.1 ");

        assertThat(requestLine.method()).isEqualTo("POST");
        assertThat(requestLine.httpVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void rejectsWrongPartCount() {
        for (String line : List.of("", "GET", "GET /login", "GET /login HTTP/1.1 EXTRA")) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> RequestLine.parse(line));
        }
    }
}
