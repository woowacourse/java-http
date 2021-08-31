package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.web.handler.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @DisplayName("인덱스 페이지를 확인")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /index HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        final MockSocket socket = createRequestHandlerAndRun(httpRequest);

        // then
        String resource = "static/index.html";
        String header = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 4416 \r\n" +
            "\r\n";

        assertResponseIsEqueal(socket, resource, header);
    }

    @DisplayName("html 요청 시 인덱스 페이지를 확인")
    @Test
    void indexHtml() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        final MockSocket socket = createRequestHandlerAndRun(httpRequest);

        // then
        String resource = "static/index.html";
        String header = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 4416 \r\n" +
            "\r\n";

        assertResponseIsEqueal(socket, resource, header);
    }

    @DisplayName("css 자원 요청 확인")
    @Test
    void cssTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        final MockSocket socket = createRequestHandlerAndRun(httpRequest);

        String resource = "static/css/styles.css";
        String header = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/css \r\n" +
            "Content-Length: 212515 \r\n" +
            "\r\n";
        // then
        assertResponseIsEqueal(socket, resource, header);
    }

    @DisplayName("svg 자원 요청 확인")
    @Test
    void svgTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /assets/img/error-404-monochrome.svg HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        final MockSocket socket = createRequestHandlerAndRun(httpRequest);

        String resource = "static/assets/img/error-404-monochrome.svg";
        String header = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: image/svg+xml \r\n" +
            "Content-Length: 6568 \r\n" +
            "\r\n";
        // then
        assertResponseIsEqueal(socket, resource, header);
    }

    private MockSocket createRequestHandlerAndRun(String httpRequest) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);
        requestHandler.run();
        return socket;
    }

    private void assertResponseIsEqueal(MockSocket socket, String resourcePath, String header)
        throws IOException {
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        String expected = header +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }
}
