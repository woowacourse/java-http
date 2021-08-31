package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ControllersTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private Controllers controllers;
    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() {
        controllers = Controllers.loadContext();
    }

    @DisplayName("split 메서드를 이용해 URI을 파싱한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/login", "/login/abc/f", "/login/"})
    void splitUri(String requestUri) {
        // given
        String expectResult = "login";

        // when
        String[] splitedUri = requestUri.split("/");

        // then
        assertThat(splitedUri[1]).isEqualTo(expectResult);
    }

    @DisplayName("URI에 따라 컨트롤러를 찾아 요청을 수행한다.")
    @Nested
    class FindByUri {

        @DisplayName("/login")
        @Nested
        class Login {

            private static final String URI = "/login";

            @DisplayName("GET 요청")
            @Test
            void get() throws IOException {
                // given
                String requestUri = String.format("GET %s HTTP/1.1", URI);
                String requestString = String.join(NEW_LINE, requestUri, "", "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 200 OK \n"
                    + "Content-Length: 13 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "login is good";

                // when
                HttpResponse httpResponse = controllers.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }

            @DisplayName("POST 요청")
            @Test
            void post() throws IOException {
                // given
                String requestUri = String.format("POST %s HTTP/1.1", URI);
                String requestBody = "account=gugu&password=password";
                int contentLength = requestBody.getBytes(StandardCharsets.UTF_8).length;
                String requestString = String.join(NEW_LINE, requestUri,
                    String.format("Content-Length: %d", contentLength), "", requestBody);
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 302 Found \n"
                    + "Location: /index.html ";

                // when
                HttpResponse httpResponse = controllers.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }
        }

        @DisplayName("/register")
        @Nested
        class Register {

            private static final String URI = "/register";

            @DisplayName("GET 요청")
            @Test
            void get() throws IOException {
                // given
                String requestUri = String.format("GET %s HTTP/1.1", URI);
                String requestString = String.join(NEW_LINE, requestUri, "", "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 200 OK \n"
                    + "Content-Length: 16 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "register is good";

                // when
                HttpResponse httpResponse = controllers.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }

            @DisplayName("POST 요청")
            @Test
            void post() throws IOException {
                // given
                String requestUri = String.format("POST %s HTTP/1.1", URI);
                String requestBody = "account=holy&password=moly&email=cool@gmail.com";
                int contentLength = requestBody.getBytes(StandardCharsets.UTF_8).length;
                String requestString = String.join(NEW_LINE, requestUri,
                    String.format("Content-Length: %d", contentLength), "", requestBody);
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 301 Moved Permanently \n"
                    + "Location: /index.html ";

                // when
                HttpResponse httpResponse = controllers.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }
        }

        @DisplayName("그 외 모든 요청은 정적 자원 요청으로 처리한다.")
        @Nested
        class StaticResource {

            @DisplayName("GET 요청")
            @Test
            void get() throws IOException {
                // given
                String requestString = String.join(NEW_LINE, "GET /static-page.html HTTP/1.1", "", "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 200 OK \n"
                    + "Content-Length: 20 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "static page is good!";

                // when
                HttpResponse httpResponse = controllers.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }

            @DisplayName("POST 요청")
            @Test
            void post() throws IOException {
                // given
                String requestString = String.join(NEW_LINE, "POST /static-page.html HTTP/1.1", "", "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 200 OK \n"
                    + "Content-Length: 20 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "static page is good!";

                // when
                HttpResponse httpResponse = controllers.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}