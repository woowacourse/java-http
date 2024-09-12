package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.servlet.DispatcherServlet;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

public class ControllerTest {

    @DisplayName("GET 요청 테스트")
    @Nested
    class GetRequest {

        @DisplayName("GET / 요청 시 index.html을 반환한다")
        @Test
        void returnIndexHtml() throws URISyntaxException, IOException {
            String httpRequest = createRequestByHttpMethodAndTarget(HttpMethod.GET, "/");
            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new DispatcherServlet());

            processor.process(socket);

            final URL url = getClass().getClassLoader().getResource("static/index.html");
            final String expected = createResponseByHttpStatusCodeAndExtensionAndUri(
                    HttpStatus.OK,
                    "html;charset=utf-8",
                    url
            );

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("GET /login 요청 시 login.html을 반환한다")
        @Test
        void returnLoginHtml() throws URISyntaxException, IOException {
            String httpRequest = createRequestByHttpMethodAndTarget(HttpMethod.GET, "/login");
            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new DispatcherServlet());
            processor.process(socket);

            final URL url = getClass().getClassLoader().getResource("static/login.html");
            final String expected = createResponseByHttpStatusCodeAndExtensionAndUri(
                    HttpStatus.OK,
                    "html;charset=utf-8",
                    url
            );

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("GET /register 요청 시 register.html을 반환한다")
        @Test
        void returnRegisterHtml() throws URISyntaxException, IOException {
            String httpRequest = createRequestByHttpMethodAndTarget(HttpMethod.GET, "/register");
            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new DispatcherServlet());
            processor.process(socket);

            final URL url = getClass().getClassLoader().getResource("static/register.html");
            final String expected = createResponseByHttpStatusCodeAndExtensionAndUri(
                    HttpStatus.OK,
                    "html;charset=utf-8",
                    url
            );

            assertThat(socket.output()).isEqualTo(expected);
        }

        private String createRequestByHttpMethodAndTarget(HttpMethod method, String target) {
            return String.join("\r\n",
                    String.format("%s %s HTTP/1.1 ", method.toString(), target),
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    ""
            );
        }
    }

    @DisplayName("POST 요청 테스트")
    @Nested
    class PostRequest {

        @DisplayName("POST /login 요청이 성공하면 302응답 코드와 index.html을 반환하고 세션을 설정한다.")
        @Test
        void loginSuccess() {
            String httpRequest = createPostRequestWithFormData("/login", "account=gugu&password=password");
            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new DispatcherServlet());
            processor.process(socket);

            assertAll(
                    () -> assertThat(socket.output()).contains("HTTP/1.1 302 Found"),
                    () -> assertThat(socket.output()).contains("Location: /index.html"),
                    () -> assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=")
            );
        }

        @DisplayName("POST /login 요청이 실패하면 401.html을 반환한다.")
        @Test
        void loginFail() throws URISyntaxException, IOException {
            String httpRequest = createPostRequestWithFormData("/login", "account=gugu&password=1");
            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new DispatcherServlet());
            processor.process(socket);

            final URL url = getClass().getClassLoader().getResource("static/401.html");
            String expected = createResponseByHttpStatusCodeAndExtensionAndUri(
                    HttpStatus.OK,
                    "html;charset=utf-8",
                    url
            );

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("POST /register 요청이 성공하면 302응답 코드와 index.html을 반환한다.")
        @Test
        void registerSuccess() {
            String httpRequest = createPostRequestWithFormData(
                    "/register",
                    "account=kaki&email=kaki@email.com&password=123"
            );
            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new DispatcherServlet());
            processor.process(socket);

            assertAll(
                    () -> assertThat(socket.output()).contains("HTTP/1.1 302 Found"),
                    () -> assertThat(socket.output()).contains("Location: /index.html")
            );
        }

        private String createPostRequestWithFormData(String target, String formData) {
            return String.join("\r\n",
                    String.format("POST %s HTTP/1.1", target),
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Type: application/x-www-form-urlencoded",
                    String.format("Content-Length: %d", formData.length()),
                    "",
                    formData
            );
        }
    }

    private String createResponseByHttpStatusCodeAndExtensionAndUri(
            HttpStatus status,
            String extension,
            URL url
    ) throws URISyntaxException, IOException {
        String file = Files.readString(Path.of(url.toURI()));
        return String.join("\r\n",
                String.format("HTTP/1.1 %d %s ", status.getCode(), status.getText()),
                String.format("Content-Type: text/%s ", extension),
                String.format("Content-Length: %d ", file.getBytes().length),
                "",
                file
        );
    }
}
