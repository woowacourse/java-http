package org.apache.coyote.http11;

import nextstep.fixtures.ServletContainerFixtures;
import org.apache.catalina.core.ServletContainer;
import org.apache.coyote.HttpStatus;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static nextstep.fixtures.HttpFixtures.요청을_생성한다;
import static nextstep.fixtures.HttpFixtures.응답을_생성한다;
import static org.apache.coyote.HttpMethod.GET;
import static org.apache.coyote.HttpMime.*;
import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void 루트_접속시_환영문구를_반환한다() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, ServletContainerFixtures.기본_URI로_생성());
        final String expected = 응답을_생성한다(HttpStatus.OK, "text/html", "Hello world!");

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void html_파일을_불러올_수_있다() throws IOException {
        // given
        final String httpRequest = 요청을_생성한다(GET, "/index.html", TEXT_HTML);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, ServletContainerFixtures.기본_URI로_생성());
        final String content = readContent("static/index.html");
        final String expected = 응답을_생성한다(HttpStatus.OK, "text/html", content);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void CSS_파일을_불러올_수_있다() throws IOException {
        // given
        final String httpRequest = 요청을_생성한다(GET, "/css/styles.css", TEXT_CSS);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, ServletContainerFixtures.기본_URI로_생성());
        final String content = readContent("static/css/styles.css");
        final String expected = 응답을_생성한다(HttpStatus.OK, "text/css", content);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void JS_파일을_불러올_수_있다() throws IOException {
        // given
        final String httpRequest = 요청을_생성한다(GET, "/js/scripts.js", TEXT_JAVASCRIPT);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, ServletContainerFixtures.기본_URI로_생성());
        final String content = readContent("static/js/scripts.js");
        final String expected = 응답을_생성한다(HttpStatus.OK, "text/javascript", content);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 파일을_찾지_못하면_NOT_FOUND가_발생한다() throws IOException {
        // given
        final String httpRequest = 요청을_생성한다(GET, "/notfound.html", TEXT_HTML);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, ServletContainerFixtures.기본_URI로_생성());
        final String content = readContent("static/404.html");
        final String expected = 응답을_생성한다(HttpStatus.NOT_FOUND, "text/html", content);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 요청을_처리할_서블릿을_찾지_못하면_404페이지를_반환한다() throws IOException {
        // given
        final String httpRequest = 요청을_생성한다(GET, "/", TEXT_HTML);
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new ServletContainer());
        final String content = readContent("static/404.html");
        final String expected = 응답을_생성한다(HttpStatus.NOT_FOUND, "text/html", content);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String readContent(final String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        assert resource != null;
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
