package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.connector.CatalinaAdapter;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void getRootTest() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new CatalinaAdapter());

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                                   "HTTP/1.1 200 OK ",
                                   "Content-Length: 13 ",
                                   "Content-Type: text/html;charset=utf-8 ",
                                   "",
                                   "Hello world!"
                                   + "\n");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void getIndexTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                                               "GET /index.html HTTP/1.1 ",
                                               "Host: localhost:8080 ",
                                               "Connection: keep-alive ",
                                               "",
                                               "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new CatalinaAdapter());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                       "Content-Length: 5564 \r\n" +
                       "Content-Type: text/html;charset=utf-8 \r\n" +
                       "\r\n" +
                       new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void getLoginTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                                               "GET /login HTTP/1.1 ",
                                               "Host: localhost:8080 ",
                                               "Connection: keep-alive ",
                                               "",
                                               "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new CatalinaAdapter());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                       "Content-Length: 3797 \r\n" +
                       "\r\n" +
                       new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void postLoginTest() {
        // given
        final String httpRequest = String.join("\r\n",
                                               "POST /login HTTP/1.1 ",
                                               "Host: localhost:8080 ",
                                               "Connection: keep-alive ",
                                               "Content-Length: 30 ",
                                               "Content-Type: application/x-www-form-urlencoded ",
                                               "",
                                               "account=gugu&password=password",
                                               "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new CatalinaAdapter());

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                       "Content-Length: 0 \r\n" +
                       "Location: /index.html \r\n" +
                       "Set-Cookie: JSESSIONID=";

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void getRegisterTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                                               "GET /register HTTP/1.1 ",
                                               "Host: localhost:8080 ",
                                               "Connection: keep-alive ",
                                               "",
                                               "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new CatalinaAdapter());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                       "Content-Length: 4319 \r\n" +
                       "\r\n" +
                       new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void postRegisterTest() {
        // given
        final String httpRequest = String.join("\r\n",
                                               "POST /register HTTP/1.1 ",
                                               "Host: localhost:8080 ",
                                               "Connection: keep-alive ",
                                               "Content-Length: 53 ",
                                               "Content-Type: application/x-www-form-urlencoded ",
                                               "",
                                               "account=chomang&email=chomang%40woowa.net&password=1234",
                                               "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new CatalinaAdapter());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 302 Found \r\n" +
                       "Content-Length: 0 \r\n" +
                       "Location: /login.html \r\n" +
                       "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
