package org.apache.coyote.http11;

import com.techcourse.Application;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    private static final String SESSION_ID = "00000000-0000-0000-0000-000000000001";

    private final RequestMapping requestMapping = Application.createRequestMapping();
    private final SessionManager sessionManager = new SessionManager();

    @Test
    void process() {
        // given
        final var socket = new StubSocket(String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + SESSION_ID,
                "",
                ""));
        final var processor = new Http11Processor(socket, requestMapping, sessionManager);
        sessionManager.add(new Session(SESSION_ID));

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
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + SESSION_ID,
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestMapping, sessionManager);
        sessionManager.add(new Session(SESSION_ID));

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] expectedBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " + expectedBody.length + "\r\n" +
                "\r\n"+
                new String(expectedBody, StandardCharsets.UTF_8);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
