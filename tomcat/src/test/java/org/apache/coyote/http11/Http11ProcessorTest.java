package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("HTTP/1.1 요청을 처리한다")
    @Nested
    class Index {
        @DisplayName("/index.html 요청")
        @Test
        void index() throws IOException {
            // given
            final String httpRequest = String.join(
                    "\r\n",
                    "GET /index.html HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "",
                    ""
            );

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/index.html");
            final String expectedBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            final String actual = socket.output();

            assertThat(actual).startsWith("HTTP/1.1 200 OK\r\n");
            assertThat(actual).contains("Content-Type: text/html;charset=utf-8\r\n");
            assertThat(actual).contains("Content-Length: 5670\r\n");

            final String actualBody = actual.substring(actual.indexOf("\r\n\r\n") + 4);
            assertEquals(expectedBody, actualBody);
        }

        @DisplayName("/ 요청")
        @Test
        void process() {
            // given
            final var socket = new StubSocket();
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final String actual = socket.output();

            assertThat(actual).startsWith("HTTP/1.1 200 OK\r\n");
            assertThat(actual).contains("Content-Type: text/html;charset=utf-8\r\n");
            assertThat(actual).contains("Content-Length: 12\r\n");
            assertThat(actual).contains("\r\n\r\n");

            final String actualBody = actual.substring(actual.indexOf("\r\n\r\n") + 4);
            assertEquals("Hello world!", actualBody);
        }
    }

}
