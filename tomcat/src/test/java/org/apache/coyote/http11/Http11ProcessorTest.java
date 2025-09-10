package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        String output = socket.output();
        assertThat(output).contains("HTTP/1.1 200 OK");
        assertThat(output).contains("Content-Type: text/html;charset=utf-8");
        assertThat(output).contains("Set-Cookie: JSESSIONID=");
        assertThat(output).contains("Hello world!");
    }

    @Test
    void index() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        String output = socket.output();
        assertThat(output).contains("HTTP/1.1 200 OK");
        assertThat(output).contains("Content-Type: text/html;charset=utf-8");
        assertThat(output).contains("Content-Length: 5564");
        assertThat(output).contains("<title>대시보드</title>");
    }
}
