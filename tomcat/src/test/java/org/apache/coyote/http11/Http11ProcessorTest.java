package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.apache.coyote.http11.utils.FileUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("/로 요청 보내는 경우")
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);
        final String expectedStatusLine = "HTTP/1.1 200 OK ";
        final String expectedContentType = "Content-Type: text/plain;charset=utf-8 ";
        final String expectedContentLength = "Content-Length: 12 ";
        final String expectedBody = "Hello world!";

        // when
        processor.process(socket);

        // then
        final String actual = socket.output();

        assertThat(actual).contains(expectedStatusLine);
        assertThat(actual).contains(expectedContentType);
        assertThat(actual).contains(expectedContentLength);
        assertThat(actual).contains(expectedBody);
    }

    @DisplayName("/index.html 로 요청 보내는 경우")
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
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String actual = socket.output();
        final File file = FileUtil.findFile("/index.html");
        final String expectedFileContents = FileUtil.generateFile(file);
        final String expectedStatusLine = "HTTP/1.1 200 OK ";
        final String expectedContentType = "Content-Type: text/html;charset=utf-8 ";
        final String expectedContentLength = "Content-Length: " + expectedFileContents.getBytes().length;

        assertThat(actual).contains(expectedStatusLine);
        assertThat(actual).contains(expectedContentType);
        assertThat(actual).contains(expectedContentLength);
        assertThat(actual).contains(expectedFileContents);
    }


    @DisplayName("JSESSIONID를 포함해 로그인 요청보내는 경우")
    @Test
    void requestWithJSESSIONID() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String actual = socket.output();
        final String expectedStatusLine = "HTTP/1.1 302 ";
        final String expectedSetCookie = "Set-Cookie: JSESSIONID=";

        assertThat(actual).contains(expectedStatusLine);
        assertThat(actual).doesNotContain(expectedSetCookie);
    }
}
