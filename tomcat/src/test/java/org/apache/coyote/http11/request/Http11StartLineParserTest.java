package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11StartLineParserTest {

    @Test
    @DisplayName("정상적인 HTTP 요청에서 start line을 잘 파싱하는지 확인")
    void parseStartLine() {
        String requestMessage = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                \r
                """;
        Http11StartLineParser startLineParser = new Http11StartLineParser();
        String startLine = startLineParser.parseStartLine(requestMessage);

        assertThat(startLine).isEqualTo("GET /index.html HTTP/1.1");
    }

    @Test
    @DisplayName("비 정상적인 HTTP 요청에서 start line을 잘 파싱하는지 확인")
    void parseInvalidStartLine() {
        String requestMessage = """
                /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                \r
                """;
        Http11StartLineParser startLineParser = new Http11StartLineParser();

        assertThatThrownBy(() -> startLineParser.parseStartLine(requestMessage))
                .hasMessage("올바른 HTTP 요청이 아닙니다.");
    }
}
