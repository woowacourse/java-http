package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import org.apache.coyote.http11.utils.FileUtil;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

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
        String actual = socket.output();

        assertThat(actual).contains(expectedStatusLine);
        assertThat(actual).contains(expectedContentType);
        assertThat(actual).contains(expectedContentLength);
        assertThat(actual).contains(expectedBody);
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
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final File file = FileUtil.findFile("/index.html");
        final String expectedFileContents = FileUtil.generateFile(file);
        final String expectedStatusLine = "HTTP/1.1 200 OK ";
        final String expectedContentType = "Content-Type: text/html;charset=utf-8 ";
        final String expectedContentLength = "Content-Length: " + expectedFileContents.getBytes().length;

        assertThat(socket.output()).contains(expectedStatusLine);
        assertThat(socket.output()).contains(expectedContentType);
        assertThat(socket.output()).contains(expectedContentLength);
        assertThat(socket.output()).contains(expectedFileContents);
    }
}
