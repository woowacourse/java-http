package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @Nested
    class RequestLine {

        @Test
        void parsesMethodPathAndVersion() throws Exception {
            // given
            String rawRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1",
                    "Host: localhost:8080",
                    "",
                    "");

            // when
            HttpRequest request = HttpRequestParser.parse(
                    new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)));

            // then
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/index.html");
            assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
        }
    }

    @Nested
    class Headers {

        @Test
        void parsesHeaderNamesAndValuesContainingColon() throws Exception {
            // given
            String rawRequest = String.join("\r\n",
                    "GET / HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "",
                    "");

            // when
            HttpRequest request = HttpRequestParser.parse(
                    new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)));

            // then
            assertThat(request.getHeaders())
                    .containsEntry("Host", "localhost:8080")
                    .containsEntry("Connection", "keep-alive");
        }
    }

    @Nested
    class Body {

        @Test
        void returnsEmptyBodyWhenContentLengthIsMissing() throws Exception {
            // given
            String rawRequest = String.join("\r\n",
                    "GET / HTTP/1.1",
                    "Host: localhost:8080",
                    "",
                    "");

            // when
            HttpRequest request = HttpRequestParser.parse(
                    new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)));

            // then
            assertThat(request.getResponseBody()).isEmpty();
        }

        @Test
        void readsBodyUsingContentLength() throws Exception {
            // given
            String body = "account=gugu&password=password";
            String rawRequest = String.join("\r\n",
                    "POST /login HTTP/1.1",
                    "Content-Length: " + body.length(),
                    "",
                    body);

            // when
            HttpRequest request = HttpRequestParser.parse(
                    new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)));

            // then
            assertThat(request.getResponseBody()).isEqualTo(body);
        }
    }
}
