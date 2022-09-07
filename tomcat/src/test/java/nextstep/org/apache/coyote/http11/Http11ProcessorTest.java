package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expectedStatusLine = "HTTP/1.1 200 OK ";
        String[] expectedHeaders = new String[] {
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 12 "};
        String expectedBody = "Hello world!";

        String[] actual = socket.output().split("\r\n");

        String actualStatusLine = actual[0];
        String[] actualHeaders = Arrays.copyOfRange(actual, 1, actual.length - 2);
        String actualBody = actual[actual.length - 1];

        assertThat(actualStatusLine).isEqualTo(expectedStatusLine);
        assertThat(actualHeaders).containsOnly(expectedHeaders);
        assertThat(actualBody).isEqualTo(expectedBody);
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

        String expectedStatusLine = "HTTP/1.1 200 OK ";
        String[] expectedHeaders = new String[] {
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 5564 "};
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String[] actual = socket.output().split("\r\n");

        String actualStatusLine = actual[0];
        String[] actualHeaders = Arrays.copyOfRange(actual, 1, actual.length - 2);
        String actualBody = actual[actual.length - 1];

        assertThat(actualStatusLine).isEqualTo(expectedStatusLine);
        assertThat(actualHeaders).containsOnly(expectedHeaders);
        assertThat(actualBody).isEqualTo(expectedBody);
    }
}
