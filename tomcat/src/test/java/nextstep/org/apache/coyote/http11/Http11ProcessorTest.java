package nextstep.org.apache.coyote.http11;

import org.apache.http.HttpStatus;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void 루트_접속시_환영문구를_반환한다() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = makeResponse(HttpStatus.OK, "text/html", 12, "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void html_파일을_불러올_수_있다() throws IOException {
        // given
        final String httpRequest = makeGetRequest("/index.html", "text/html");


        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String content = readContent("static/index.html");
        final String expected = makeResponse(HttpStatus.OK, "text/html", 5564, content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void CSS_파일을_불러올_수_있다() throws IOException {
        // given
        final String httpRequest = makeGetRequest("/css/styles.css", "text/css");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String content = readContent("static/css/styles.css");
        final String expected = makeResponse(HttpStatus.OK, "text/css", 211991, content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void JS_파일을_불러올_수_있다() throws IOException {
        // given
        final String httpRequest = makeGetRequest("/js/scripts.js", "*/*");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String content = readContent("static/js/scripts.js");
        final String expected = makeResponse(HttpStatus.OK, "*/*", 976, content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 파일을_찾지_못하면_BadRequest가_발생한다() {
        // given
        final String httpRequest = makeGetRequest("/notfound.html", "text/html");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = makeResponse(HttpStatus.BAD_REQUEST, "text/html", 0, "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String makeGetRequest(final String uri, final String contentType) {
        return String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: " + contentType + ",*/*;q=0.1",
                "",
                "");
    }

    private String makeResponse(final HttpStatus httpStatus, final String contentType, final int contentLength, final String content) {
        return "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.name() + " \r\n" +
                "Content-Type: " + contentType + ";charset=utf-8 \r\n" +
                "Content-Length: " + contentLength + " \r\n" +
                "\r\n" +
                content;
    }

    private String readContent(final String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
