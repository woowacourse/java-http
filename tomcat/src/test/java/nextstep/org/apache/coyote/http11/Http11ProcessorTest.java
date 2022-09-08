package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private StubSocket httpGet(final String url) {
        final String httpRequest = String.join("\r\n",
                "GET " + url + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        return new StubSocket(httpRequest);
    }

    private StubSocket httpPost(final String url, final String responseBody) {
        final String httpRequest = String.join("\r\n",
                "POST " + url + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody,
                "");
        return new StubSocket(httpRequest);
    }

    @Test
    void defaultForward() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/strings ",
                "Content-Length: 12 ",
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
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5518 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() {
        StubSocket socket = httpGet("/login?account=gugu&password=password");
        final Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        String httpResponse = socket.output();
        assertAll(
                () -> assertThat(httpResponse).contains("302"),
                () -> assertThat(httpResponse).contains("Set-Cookie")
        );
    }

    @Test
    void loginUserNotFoundFailed() {
        StubSocket socket = httpGet("/login?account=hello&password=password");
        final Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        assertThat(socket.output()).contains("400");
    }

    @Test
    void loginUserUnauthorizedFailed() {
        StubSocket socket = httpGet("/login?account=gugu&password=asdf");
        final Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        assertThat(socket.output()).contains("401");
    }

    @Test
    void create() {
        StubSocket socket = httpPost("/register", "account=huni&password=password&email=huni%40woowahan.com");
        final Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        assertAll(
                () -> assertThat(socket.output()).contains("302"),
                () -> assertThat(socket.output()).contains("/index.html")
        );

    }

    @Test
    void createAlreadyRegisteredFailed() {
        StubSocket socket = httpPost("/register", "account=gugu&password=password&email=hkkang%40woowahan.com");
        final Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        assertThat(socket.output()).contains("400");
    }
}
