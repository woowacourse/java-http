package nextstep.jwp;

import nextstep.jwp.webserver.RequestHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expectedBody = getResponseBody("static/index.html");
        String expected = String.join("\r\n",
                "HTTP/1.1 200 Ok",
                "Set-Cookie: JSESSIONID=[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}",
                "Content-Length: " + expectedBody.getBytes().length,
                "Content-Type: text/html;charset=utf-8",
                "",
                expectedBody);
        assertThat(socket.output()).containsPattern(expected);
    }

    @Test
    void login() {
        // given
        String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                body);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Set-Cookie: JSESSIONID=[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}",
                "Location: /index.html",
                "",
                ""
        );
        assertThat(socket.output()).containsPattern(expected);
    }

    @Test
    void getRegister() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expectedRequestLine = "HTTP/1.1 200 Ok";
        String expectedBody = getResponseBody("static/register.html");
        List<String> headers = Arrays.asList(
                "Content-Length: " + expectedBody.getBytes().length,
                "Content-Type: text/html;charset=utf-8"
        );
        String setCookieRegex = "Set-Cookie: JSESSIONID=[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

        assertThat(socket.output()).startsWith(expectedRequestLine);
        assertThat(socket.output()).containsPattern(setCookieRegex);
        assertThat(socket.output()).contains(headers);
        assertThat(socket.output()).endsWith(expectedBody);
    }

    @Test
    void postRegister() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Set-Cookie: JSESSIONID=[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}",
                "Location: /index.html",
                "",
                ""
        );
        assertThat(socket.output()).containsPattern(Pattern.compile(expected));
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive",
                "Content-Type: text/css;charset=utf-8",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expectedRequestLine = "HTTP/1.1 200 Ok";
        String expectedBody = getResponseBody("static/css/styles.css");
        List<String> headers = Arrays.asList(
                "Content-Length: " + expectedBody.getBytes().length,
                "Content-Type: text/css;charset=utf-8"
        );
        String setCookieRegex = "Set-Cookie: JSESSIONID=[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

        assertThat(socket.output()).startsWith(expectedRequestLine);
        assertThat(socket.output()).containsPattern(setCookieRegex);
        assertThat(socket.output()).contains(headers);
        assertThat(socket.output()).endsWith(expectedBody);
    }

    private String getResponseBody(String fileName) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(fileName);
        return new String(Files.readAllBytes(
                new File(Objects.requireNonNull(resource).getFile()).toPath()));
    }
}
