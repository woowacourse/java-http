package nextstep.jwp;

import static nextstep.jwp.TestFixture.runRequestHandler;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StaticFileControllerTest {

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css; charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void js() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: application/javascript; charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void notFoundPath() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /notfoundpath HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void notFoundFile() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /notfoundfile.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void cookie() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "Set-Cookie: JSESSIONID=";
        assertThat(output).startsWith(expected);
    }
}
