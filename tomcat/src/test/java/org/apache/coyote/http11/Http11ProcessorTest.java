package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestFixtures.*;

class Http11ProcessorTest {

    private void processRequest(String httpRequest, String[] expectedResponses) {
        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        processor.process(socket);

        String response = socket.output();
        for (String expectedResponse : expectedResponses) {
            assertThat(response).contains(expectedResponse);
        }
    }

    private String buildHttpRequest(String method, String path, String body) {
        String requestLine = String.join(" ",
                method,
                path,
                "HTTP/1.1");

        return String.join("\r\n",
                requestLine,
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "Connection: keep-alive",
                "",
                body);
    }

    private void testResourceResponse(String requestPath, String contentType, String expectedFilePath) throws IOException {
        // given
        final String httpRequest = buildHttpRequest(GET, requestPath, "");

        final URL resource = getClass().getClassLoader().getResource(expectedFilePath);
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.OK.getStatus(), contentType, contentLength, body});
    }

    private static Stream<Arguments> resourceProvider() {
        return Stream.of(
                Arguments.of("/", CONTENT_TYPE_HTML, "static/index.html"),
                Arguments.of("/css/styles.css", CONTENT_TYPE_CSS, "static/css/styles.css"),
                Arguments.of("/js/scripts.js", CONTENT_TYPE_JAVASCRIPT, "static/js/scripts.js"),
                Arguments.of("/assets/img/error-404-monochrome.svg", CONTENT_TYPE_SVG, "static/assets/img/error-404-monochrome.svg"),
                Arguments.of("/index.html", CONTENT_TYPE_HTML, "static/index.html")
        );
    }

    @ParameterizedTest
    @MethodSource("resourceProvider")
    @DisplayName("정적 자원 요청에 대한 응답을 검증한다.")
    void resourceResponse(String requestPath, String contentType, String expectedFilePath) throws IOException {
        testResourceResponse(requestPath, contentType, expectedFilePath);
    }

    @Test
    @DisplayName("request 처리 중 IllegalArgumentException이 발생하면 /static/400.html 페이지로 리다이렉트한다.")
    void failProcess400() {
        // given
        final String httpRequest = String.join("\r\n",
                "ERROR",
                "Host: localhost:8080",
                "");

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /400.html"});
    }

    @Test
    @DisplayName("request 처리 중 IOException이 발생하면 /static/500.html 페이지로 리다이렉트한다.")
    void processRequest_throwsIOException_returns500() {
        // given
        StubSocket socket = new StubSocket("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n") {
            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException("Test IOException");
            }
        };
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String response = socket.output();
        String status = HttpStatus.FOUND.getStatus();
        String location = "Location: /500.html";

        assertThat(response).contains(status, CONTENT_TYPE_HTML, location);
    }
}
