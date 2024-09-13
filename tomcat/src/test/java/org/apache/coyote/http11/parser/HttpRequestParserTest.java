package org.apache.coyote.http11.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http11.data.HttpMethod;
import org.apache.coyote.http11.data.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @Test
    @DisplayName("문자열 HTTP 요청을 파싱한다.")
    void parse() {
        // given
        String request = """
                GET / HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                """;

        // when & then
        try (BufferedReader reader = new BufferedReader(new StringReader(request))) {
            HttpRequest httpRequest = HttpRequestParser.parse(reader);
            assertAll(() -> {
                assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
                assertThat(httpRequest.getPath()).isEqualTo("/");
                assertThat(httpRequest.getRequestBody()).isNull();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
