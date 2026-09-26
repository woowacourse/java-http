package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceResponseTest {

    @Test
    void servesPagesAndStaticResourcesWithTheirOriginalContents() throws IOException {
        Map<String, String> resources = Map.of(
                "/login", "static/login.html",
                "/register", "static/register.html",
                "/index.html", "static/index.html",
                "/assets/img/error-404-monochrome.svg", "static/assets/img/error-404-monochrome.svg"
        );
        for (var resource : resources.entrySet()) {
            String expectedBody;
            try (var input = getClass().getClassLoader().getResourceAsStream(resource.getValue())) {
                assertThat(input).isNotNull();
                expectedBody = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
            String contentType = resource.getKey().endsWith(".svg")
                    ? "image/svg+xml" : "text/html;charset=UTF-8";

            assertResponse(resource.getKey(), "200 OK", contentType, expectedBody);
        }
    }

    @Test
    void missingStaticResourceKeepsNotFoundResponse() {
        assertResponse("/missing-resource.html", "404 Not Found",
                "text/plain;charset=UTF-8", "요청한 파일을 찾을 수 없습니다.");
    }

    private void assertResponse(String path, String status, String contentType, String body) {
        StubSocket socket = new StubSocket("GET " + path + " HTTP/1.1\r\nCookie: JSESSIONID=existing\r\n\r\n");
        new Http11Processor(socket, new SessionManager()).process(socket);

        String[] response = socket.output().split("\r\n\r\n", 2);
        assertThat(response).hasSize(2);
        String[] headers = response[0].split("\r\n");
        assertThat(headers[0]).isEqualTo("HTTP/1.1 " + status);
        assertThat(headers).contains(
                "Content-Type: " + contentType,
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length
        );
        assertThat(response[1]).isEqualTo(body);
    }
}
