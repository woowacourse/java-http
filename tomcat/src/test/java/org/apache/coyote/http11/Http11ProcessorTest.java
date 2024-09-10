package org.apache.coyote.http11;

import org.apache.catalina.controller.ControllerRegistry;
import org.apache.catalina.controller.DefaultExceptionHandler;
import org.apache.catalina.session.UuidSessionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @BeforeEach
    void setUp() {
        ControllerRegistry.registerControllers("com.techcourse.controller");
    }

    @Nested
    class ResourceFileTest {
        @DisplayName("HTML 파일을 반환한다.")
        @ParameterizedTest
        @ValueSource(strings = {"/login.html", "/register.html"})
        void getResourceHtml(String requestPath) throws IOException {
            String httpRequest = String.join("\r\n",
                    "GET " + requestPath + " HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new UuidSessionGenerator(), new DefaultExceptionHandler());

            processor.process(socket);

            String responseBody = getFile(requestPath);
            int contentLength = responseBody.getBytes().length;

            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    "Content-Length: " + contentLength + " \r\n",
                    responseBody
            );
        }

        @DisplayName("CSS 파일을 반환한다.")
        @Test
        void getCssFile() throws IOException {
            String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new UuidSessionGenerator(), new DefaultExceptionHandler());

            processor.process(socket);

            String responseBody = getFile("/css/styles.css");
            int contentLength = responseBody.getBytes().length;

            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/css;charset=utf-8 \r\n",
                    "Content-Length: " + contentLength + " \r\n",
                    responseBody
            );
        }

        private String getFile(String fileName) throws IOException {
            URL resource = getClass().getClassLoader().getResource("static/" + fileName);
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        }
    }
}
