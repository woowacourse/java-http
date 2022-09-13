package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import nextstep.jwp.ServletConfigurationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @BeforeEach
    void setUp() {
        final var servletConfiguration = new ServletConfigurationImpl();
        servletConfiguration.addServlet();
        servletConfiguration.addRequestHandler();
        servletConfiguration.addExceptionResolver();
    }

    @Test
    void process() {
        // given
        try (final var socket = new StubSocket()) {
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = String.join(System.lineSeparator(),
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ");

            assertThat(socket.output()).contains(expected);
        } catch (IOException e) {
        }
    }

    @Test
    void index() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        try (final var socket = new StubSocket(httpRequest)) {
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            var expected = String.join(System.lineSeparator(),
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "");
            assertThat(socket.output()).contains(expected);
        } catch (IOException e) {
        }
    }

    @Test
    void 회원가입을_하고_로그인을_한다() {
        // give
        var request = String.join(System.lineSeparator(),
                "Post /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-length: 30 ",
                "",
                "account=a&password=a&email=a@a");
        try (final var socket1 = new StubSocket(request)) {
            final var http11Processor = new Http11Processor(socket1);
            http11Processor.process(socket1);

            request = String.join(System.lineSeparator(),
                    "Post /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-length: 20 ",
                    "",
                    "account=a&password=a");
            try (final var socket2 = new StubSocket(request)) {
                // when
                http11Processor.process(socket2);

                // then
                assertThat(socket2.output()).contains("HTTP/1.1 302 Found ", "Location: /index.html");
            }
        } catch (IOException ignore) {
        }
    }

    @Test
    void 로그인시_비밀번호가_틀린경우_401_페이지로_리다이렉트한다() {
        // given
        final var request = String.join(System.lineSeparator(),
                "Post /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-length: 23 ",
                "",
                "account=gugu&password=a");
        try (final var stubSocket = new StubSocket(request)) {
            final var http11Processor = new Http11Processor(stubSocket);

            // when
            http11Processor.process(stubSocket);

            // then
            assertThat(stubSocket.output()).contains("HTTP/1.1 302 Found ", "Location: /401.html");
        } catch (IOException ignore) {
        }
    }
}
