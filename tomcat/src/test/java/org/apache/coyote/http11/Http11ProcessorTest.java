package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.catalina.route.DefaultDispatcher;
import org.apache.catalina.route.RequestMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("HTTP/1.1 요청을 처리한다.")
    @Test
    void process() {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = getProcessor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Length: 0",
                "", "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("정적 파일 응답 테스트")
    @Nested
    class StaticResourceTest {

        @DisplayName("HTML 파일을 응답한다.")
        @ParameterizedTest
        @ValueSource(strings = {"/login.html", "/register.html"})
        void htmlFile(String requestPath) throws IOException {
            // given
            String httpRequest = String.join("\r\n",
                    "GET " + requestPath + " HTTP/1.1 ",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "\r\n"
            );

            // when
            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = getProcessor(socket);
            processor.process(socket);

            // then
            String responseBody = readFileContent(requestPath);
            int contentLength = responseBody.getBytes().length;
            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK\r\n",
                    "Content-Length: " + contentLength + "\r\n",
                    "Content-Type: text/html;charset=utf-8\r\n",
                    responseBody
            );
        }

        @DisplayName("CSS 파일을 응답한다.")
        @Test
        void cssFile() throws IOException {
            // given
            String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "\r\n"
            );

            // when
            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = getProcessor(socket);
            processor.process(socket);

            // then
            String responseBody = readFileContent("/css/styles.css");
            int contentLength = responseBody.getBytes().length;
            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK\r\n",
                    "Content-Length: " + contentLength + "\r\n",
                    "Content-Type: text/css;charset=utf-8\r\n",
                    responseBody
            );
        }

        private String readFileContent(String fileName) throws IOException {
            URL resource = getClass().getClassLoader().getResource("static/" + fileName);
            return new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));
        }
    }

    private Http11Processor getProcessor(StubSocket socket) {
        return new Http11Processor(socket, new DefaultDispatcher(new RequestMapper()));
    }
}
