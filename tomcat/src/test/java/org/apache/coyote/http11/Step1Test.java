package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import support.StubSocket;

class Step1Test {

    @Test
    void 루트_요청에_Hello_world_응답을_반환한다() {
        // given
        var socket = new StubSocket();
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expectedHeaders = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ");

        assertThat(socket.output()).startsWith(expectedHeaders);
        assertThat(socket.output()).endsWith("\r\n\r\nHello world!");
    }

    @Test
    void index_html_요청에_인덱스_페이지를_반환한다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = Files.readString(new File(resource.getFile()).toPath());

        assertThat(socket.output()).endsWith("\r\n\r\n" + expected);
    }

    @Test
    void CSS_리소스_요청에_해당_파일의_내용으로_응답한다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expected = Files.readString(new File(resource.getFile()).toPath());

        assertThat(socket.output()).endsWith("\r\n\r\n" + expected);
    }

    @Test
    void CSS_리소스_요청에_text_css_Content_Type으로_응답한다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Content-Type: text/css;charset=utf-8");
    }

    @Test
    void Query_String이_있는_로그인_요청에_로그인_페이지를_반환한다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = Files.readString(new File(resource.getFile()).toPath());

        assertThat(socket.output()).endsWith("\r\n\r\n" + expected);
    }

    @Test
    void 전달된_계정_정보와_일치하는_회원_조회_결과를_로그로_남긴다() {
        // given
        Logger logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);

        String body = "account=gugu&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

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
}
