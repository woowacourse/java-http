package nextstep.jwp;

import nextstep.jwp.infrastructure.RequestHandler;
import nextstep.jwp.model.web.ContentType;
import nextstep.jwp.model.web.ResourceFinder;
import nextstep.jwp.model.web.StatusCode;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    @DisplayName("GET /index.html")
    void indexHtml() throws Exception {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] readBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " + readBytes.length + "\r\n" +
                "" + "\r\n" +
                new String(readBytes);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /login")
    void loginHtml() throws Exception {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        CustomHttpResponse response = new CustomHttpResponse();
        String resource = ResourceFinder.resource("/login.html");
        response.setStatusLine(StatusCode.OK, "HTTP/1.1");
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.HTML.getContentType());
        headers.put("Content-Length", resource.getBytes().length + "");
        response.setHeaders(headers);
        response.setResponseBody(resource);
        String expected = new String(response.getBodyBytes());
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login success")
    void loginSuccessed() throws Exception {
        // given
        String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
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
        CustomHttpResponse response = new CustomHttpResponse();
        response.setStatusLine(StatusCode.FOUND, "HTTP/1.1");
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", "/index.html");
        response.setHeaders(headers);
        String expected = new String(response.getBodyBytes());
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login failed")
    void loginFailed() throws Exception {
        // given
        String body = "account=gugu&password=nope";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
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
        CustomHttpResponse response = new CustomHttpResponse();
        response.setStatusLine(StatusCode.FOUND, "HTTP/1.1");
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", "/401.html");
        response.setHeaders(headers);
        String expected = new String(response.getBodyBytes());
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /register")
    void registerHtml() throws Exception {
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
        CustomHttpResponse response = new CustomHttpResponse();
        String resource = ResourceFinder.resource("/register.html");
        response.setStatusLine(StatusCode.OK, "HTTP/1.1");
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.HTML.getContentType());
        headers.put("Content-Length", resource.getBytes().length + "");
        response.setHeaders(headers);
        response.setResponseBody(resource);
        String expected = new String(response.getBodyBytes());
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /register")
    void register() throws Exception {
        // given
        String body = "account=bepoz&password=pwd&email=bepoz@woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                body);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        CustomHttpResponse response = new CustomHttpResponse();
        response.setStatusLine(StatusCode.FOUND, "HTTP/1.1");
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", "/login.html");
        response.setHeaders(headers);
        String expected = new String(response.getBodyBytes());
        assertThat(socket.output()).isEqualTo(expected);
    }
}
