package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class Http11ProcessorTest {

    public static final String FAIL_PAGE_EXPECTED = "HTTP/1.1 302 FOUND \r\n" +
                                                    "Content-Type: text/html;charset=utf-8 \r\n" +
                                                    "Content-Length: 3863 \r\n" +
                                                    "Location: 401.html\r\n" +
                                                    "\r\n";

    @Test
    @Disabled
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
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
        final var expected = "HTTP/1.1 200 OK \r\n" +
                             "Content-Type: text/html;charset=utf-8 \r\n" +
                             "Content-Length: 5670 \r\n" +
                             "\r\n" +
                             new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않는 account로 로그인 시 예외가 발생한다.")
    void invalidAccount() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 3863",
                "Content-Type: application/x-www.form-urlencoded ",
                "Accept: */* ",
                "",
                "account=zeze&password=password ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(FAIL_PAGE_EXPECTED);
    }

    @Test
    @DisplayName("일치하지 않는 password 로그인 시 예외가 발생한다.")
    void invalidPassword() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 3863",
                "Content-Type: application/x-www.form-urlencoded ",
                "Accept: */* ",
                "",
                "account=redddy&password=486 ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(FAIL_PAGE_EXPECTED);
    }

    @Test
    @DisplayName("로그인 실패하면 sessionId를 만들지 않는다.")
    void doNotGenerateSESSIONID() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 3863",
                "Content-Type: application/x-www.form-urlencoded ",
                "Accept: */* ",
                "",
                "account=redddy&password=486 ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).doesNotContain("JSESSIONID");
    }
}
