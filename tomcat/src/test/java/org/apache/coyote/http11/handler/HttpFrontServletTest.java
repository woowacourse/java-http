package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpFrontServletTest {

    private final HttpFrontServlet httpFrontServlet = new HttpFrontServlet(RequestServletMapping.init());

    @Nested
    @DisplayName("handle 메소드는")
    class Handle {

        @Test
        @Disabled
        @DisplayName("static file uri를 반환하는 핸들러가 실행되면 해당 파일 정보가 포함된 ResponseEntity를 반환한다.")
        void success_file() throws IOException {
            // given
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("GET /login?account=gugu&password=password HTTP/1.1");
            final HttpRequest httpRequest = HttpRequest.of(rawRequest);

            // when
            final ResponseEntity response = httpFrontServlet.service(httpRequest);

            // then
            final URL url = FileHandler.class.getClassLoader().getResource("static/login.html");
            final Path path = Path.of(url.getPath());
            final byte[] fileBytes = Files.readAllBytes(path);

            assertAll(() -> {
                assertThat(response).extracting("httpStatus", "body")
                        .containsExactly(OK, new String(fileBytes));
                assertThat(response.getHttpHeader().getHeader("Content-Type")).isEqualTo(Files.probeContentType(path));
            });
        }

        @Test
        @DisplayName("body를 반환하는 핸들러가 실행되면 해당 body 정보가 포함된 ResponseEntity를 반환한다.")
        void success_body() throws IOException {
            // given
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("GET /login?account=gugu&password=password HTTP/1.1");
            final HttpRequest httpRequest = HttpRequest.of(rawRequest);

            // when
            final ResponseEntity response = httpFrontServlet.service(httpRequest);

            // then
            assertAll(() -> {
                assertThat(response).extracting("httpStatus", "body")
                        .containsExactly(FOUND, "");
                assertThat(response.getHttpHeader().getHeader("Location")).isEqualTo("/index.html");
            });
        }

        @Test
        @DisplayName("입력 받은 path와 매핑되는 핸들러가 없다면 Redirect Not Found ResponseEntity를 반환한다.")
        void success_notFountResponse() throws IOException {
            // given
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("GET /wrong HTTP/1.1");
            final HttpRequest httpRequest = HttpRequest.of(rawRequest);

            // when
            final ResponseEntity response = httpFrontServlet.service(httpRequest);

            // then
            final URL url = FileHandler.class.getClassLoader().getResource("static/404.html");
            final Path path = Path.of(url.getPath());
            final byte[] fileBytes = Files.readAllBytes(path);

            assertAll(() -> {
                assertThat(response).extracting("httpStatus", "body")
                        .containsExactly(FOUND, "");
                assertThat(response.getHttpHeader().getHeader("Location")).isEqualTo(NOT_FOUND.getStatusCode() + ".html");
            });
        }

        @Test
        @DisplayName("매핑한 핸들러 실행 도중 IllegalArgumentException 예외가 발생하면 Redirect Not Found ResponseEntity를 반환한다.")
        void success_ServerErrorResponse() throws IOException {
            // given
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("GET /login?account=eve&password=password HTTP/1.1");
            final HttpRequest httpRequest = HttpRequest.of(rawRequest);

            // when
            final ResponseEntity response = httpFrontServlet.service(httpRequest);

            // then
            assertAll(() -> {
                assertThat(response).extracting("httpStatus", "body")
                        .containsExactly(FOUND, "");
                assertThat(response.getHttpHeader().getHeader("Location")).isEqualTo(NOT_FOUND.getStatusCode() + ".html");
            });
        }
    }
}