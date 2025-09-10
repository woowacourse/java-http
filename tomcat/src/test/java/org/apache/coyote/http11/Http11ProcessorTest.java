package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.catalina.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var tomcatController = new RequestHandler();
        final var httpRequestParser = new HttpRequestParser();
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, httpRequestParser, tomcatController);

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
    @DisplayName("로그인하지 않은 사용자는 index.html에 접근할 수 없다.")
    void index() throws IOException {
        // given
        final var tomcatController = new RequestHandler();
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var httpRequestParser = new HttpRequestParser();
        final Http11Processor processor = new Http11Processor(socket, httpRequestParser, tomcatController);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 FOUND\r\n" +
                "Location: /login.html\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 시, JSESSIONID를 발급하고 SetCookie에 담아준다")
    void loginTest() {
        // given
        final var tomcatController = new RequestHandler();
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 30",
                "",// body 길이 정확히 지정
                "account=gugu&password=password"
        );

        final var socket = new StubSocket(httpRequest);
        final var httpRequestParser = new HttpRequestParser();
        final Http11Processor processor = new Http11Processor(socket, httpRequestParser, tomcatController);

        // when
        processor.process(socket);

        // then
        var expected = "Set-Cookie: JSESSIONID=";

        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("회원가입 시, JSESSIONID를 발급하고 SetCookie에 담아준다")
    void registerTest() {
        // given
        final var tomcatController = new RequestHandler();
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 40",
                "",// body 길이 정확히 지정
                "account=gugu&password=password&email=admin@email.com"
        );

        final var socket = new StubSocket(httpRequest);
        final var httpRequestParser = new HttpRequestParser();
        final Http11Processor processor = new Http11Processor(socket, httpRequestParser, tomcatController);

        // when
        processor.process(socket);

        // then
        var expected = "Set-Cookie: JSESSIONID=";

        assertThat(socket.output()).contains(expected);
    }
}
