package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.LoggerFactory;
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
        var expected = String.join("\r\n",
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
        final String httpRequest= String.join("\r\n",
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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void CSS_리소스를_요청하면_CSS_내용을_반환한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String expected = Files.readString(new File(resource.getFile()).toPath());

        assertThat(socket.output()).endsWith("\r\n\r\n" + expected);
    }

    @Test
    void CSS_리소스_요청에_text_css_Content_Type으로_응답한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Content-Type: text/css;charset=utf-8");
    }

    @Test
    void 로그인에_성공하면_index_html로_리다이렉트한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @ParameterizedTest
    @CsvSource({
            "unknown, password",
            "gugu, wrong"
    })
    void 로그인에_실패하면_401_html로_리다이렉트한다(
            String account,
            String password
    ) {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=" + account + "&password=" + password + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302");
        assertThat(socket.output()).contains("Location: /401.html");
    }

    @Test
    void 로그인_페이지를_요청하면_로그인_페이지를_반환한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String expected = Files.readString(new File(resource.getFile()).toPath());

        assertThat(socket.output()).endsWith("\r\n\r\n" + expected);
    }

    @Test
    void 로그인_요청을_처리하면_회원_정보를_로그로_남긴다() {
        // given
        final Logger logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);

        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        try {
            // when
            processor.process(socket);

            // then
            assertThat(appender.list)
                    .extracting(ILoggingEvent::getFormattedMessage)
                    .contains("user : User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @ParameterizedTest
    @CsvSource({
            "/index.html, static/index.html",
            "/css/styles.css, static/css/styles.css",
            "/login, static/login.html",
            "/login?account=gugu&password=password, static/login.html"
    })
    void 요청_경로에_해당하는_정적_리소스를_반환한다(String requestTarget, String resourcePath) throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET " + requestTarget + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        final String expected = Files.readString(new File(resource.getFile()).toPath());

        assertThat(socket.output()).endsWith("\r\n\r\n" + expected);
    }
}
