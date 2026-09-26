package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.WebApplication;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.RequestMapping;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private final RequestMapping requestMapping = WebApplication.createRequestMapping();

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginSuccessRedirectsToIndex() {
        final var socket = new StubSocket(
                "GET /login?account=gugu&password=password HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket, requestMapping);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found\r\n")
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36}\\r\\n")
                .endsWith(String.join("\r\n",
                        "Location: /index.html",
                        "Content-Length: 0",
                        "",
                        ""));
    }

    @Test
    void loginFailureRedirectsToUnauthorizedPage() {
        final var socket = new StubSocket(
                "GET /login?account=gugu&password=wrong HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket, requestMapping);

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /401.html",
                "Content-Length: 0",
                "",
                ""));
    }

    @Test
    void loggedInUserIsRedirectedFromLoginPageToIndex() {
        final var loginSocket = new StubSocket(
                "GET /login?account=gugu&password=password HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        new Http11Processor(loginSocket, requestMapping).process(loginSocket);
        String setCookie = loginSocket.output().lines()
                .filter(line -> line.startsWith("Set-Cookie:"))
                .findFirst()
                .orElseThrow();

        final var loginPageSocket = new StubSocket(String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                setCookie.replace("Set-Cookie:", "Cookie:"),
                "",
                ""));
        new Http11Processor(loginPageSocket, requestMapping).process(loginPageSocket);

        assertThat(loginPageSocket.output()).isEqualTo(String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                ""));
    }

    @Test
    void javascriptResourceHasJavascriptContentType() {
        final var socket = new StubSocket(String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1",
                "Host: localhost:8080",
                "Accept: */*",
                "",
                ""));

        new Http11Processor(socket, requestMapping).process(socket);

        assertThat(socket.output()).startsWith(String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: application/javascript;charset=utf-8"));
    }
}
