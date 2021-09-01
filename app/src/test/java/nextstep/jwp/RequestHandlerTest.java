package nextstep.jwp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @DisplayName("[GET] /index.html")
    @Test
    void indexHtml() throws IOException {
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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[GET] /login")
    @Test
    void loginHtml() throws IOException, URISyntaxException {
        // given
        String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        Path path = Paths.get(resource.toURI());
        String resourceFile = new String(Files.readAllBytes(path));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + resourceFile.getBytes().length + " ",
                "",
                resourceFile);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[GET] /login - SUCCESS")
    @Test
    void loginSuccess() {
        // given
        String account = "gugu";
        String password = "password";
        String queryParams = "?account=" + account + "&password=" + password;
        String request = String.join("\r\n",
                "GET /login" + queryParams + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "");
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080/index.html ",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[GET] /login - NOT MATCH PASSWORD FAIL")
    @Test
    void loginFailWhenNotMatchPassword() {
        // given
        String account = "gugu";
        String password = "password1";
        String queryParams = "?account=" + account + "&password=" + password;
        String request = String.join("\r\n",
                "GET /login" + queryParams + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "");
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080/401.html ",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[GET] /login - NOT REGISTERED ACCOUNT FAIL")
    @Test
    void loginFailWhenNotRegisteredAccount() {
        // given
        String account = "roki";
        String password = "password1";
        String queryParams = "?account=" + account + "&password=" + password;
        String request = String.join("\r\n",
                "GET /login" + queryParams + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "");
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080/401.html ",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[GET] /register - SUCCESS")
    @Test
    void registerHtml() throws URISyntaxException, IOException {
        // given
        String request = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        Path path = Paths.get(resource.toURI());
        String resourceFile = new String(Files.readAllBytes(path));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + resourceFile.getBytes().length + " ",
                "",
                resourceFile);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[POST] /register - SUCCESS")
    @Test
    void registerSuccess() {
        // given
        String params = "account=roki&password=password&email=hkkang%40woowahan.com";
        String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + params.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                params);
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080/index.html ",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[POST] /register - FAIL")
    @Test
    void registerSuccessWhenAlreadyRegisteredAccount() {
        // given
        String params = "account=gugu&password=password&email=hkkang%40woowahan.com";
        String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + params.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                params);
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error ",
                "Location: http://localhost:8080/500.html ",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[GET] /foo - FAIL")
    @Test
    void notRegisteredGetRequestMapping() throws URISyntaxException {
        // given
        String request = String.join("\r\n",
                "GET /foo HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        Path path = Paths.get(resource.toURI());
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Location: http://localhost:8080/404.html ",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("[POST] /foo - FAIL")
    @Test
    void notRegisteredPOSTRequestMapping() {
        // given
        String request = String.join("\r\n",
                "POST /foo HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final MockSocket socket = new MockSocket(request);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Location: http://localhost:8080/404.html ",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
    }
}
