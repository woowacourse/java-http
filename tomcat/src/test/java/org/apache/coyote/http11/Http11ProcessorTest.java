package org.apache.coyote.http11;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import support.StubSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("HTTP/1.1 요청 처리")
class Http11ProcessorTest {

    @Nested
    @DisplayName("기본 응답과 응답 내용 위임")
    class ResponseTests {

        @Test
        @DisplayName("루트 경로를 요청하면 Hello world!를 응답한다")
        void process() {
            // given
            final var socket = new StubSocket();
            final var processor = new Http11Processor(socket);
            final String body = "Hello world!";
            final var expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("주입한 응답 내용 결정자에게 요청 경로를 전달한다")
        void usesInjectedResponseContentResolver() throws IOException {
            // given
            final var socket = new StubSocket("GET /custom HTTP/1.1\r\n\r\n");
            final var resolver = mock(ResponseContentResolver.class);
            when(resolver.resolve("/custom"))
                    .thenReturn(new ResponseContent("text/plain;charset=utf-8", "custom".getBytes(StandardCharsets.UTF_8)));
            final var processor = new Http11Processor(socket, resolver);

            // when
            processor.process(socket);

            // then
            verify(resolver).resolve("/custom");
            assertThat(socket.output())
                    .contains("Content-Type: text/plain;charset=utf-8")
                    .endsWith("\r\n\r\ncustom");
        }
    }

    @Nested
    @DisplayName("요청 형식 검사")
    class RequestParsingTests {

        @Test
        @DisplayName("요청 라인에 항목이 네 개면 응답하지 않는다")
        void requestLineWithExtraPartIsRejected() {
            // given
            final var socket = new StubSocket("GET /index.html HTTP/1.1 EXTRA\r\n\r\n");
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).isEmpty();
        }

        @Test
        @DisplayName("요청이 비어 있거나 헤더가 끝나지 않으면 응답하지 않는다")
        void incompleteRequestIsIgnored() {
            // given
            final var requests = List.of("", "GET /index.html HTTP/1.1\r\nHost: localhost:8080");
            for (final String request : requests) {
                final var socket = new StubSocket(request);
                final var processor = new Http11Processor(socket);

                // when
                processor.process(socket);

                // then
                assertThat(socket.output()).as(request).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("정적 리소스 응답")
    class StaticResourceTests {

        @Test
        @DisplayName("인덱스 페이지를 HTML로 응답한다")
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

            try (final var resource = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
                assertThat(resource).isNotNull();
                final byte[] html = resource.readAllBytes();
                final String expected = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + html.length + " ",
                        "",
                        new String(html, StandardCharsets.UTF_8));

                // when
                processor.process(socket);

                // then
                assertThat(socket.output()).isEqualTo(expected);
            }
        }

        @Test
        @DisplayName("스타일시트를 CSS로 응답한다")
        void css() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1",
                    "Host: localhost:8080",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            try (final var resource = getClass().getClassLoader().getResourceAsStream("static/css/styles.css")) {
                assertThat(resource).isNotNull();
                final byte[] css = resource.readAllBytes();
                final String expectedHeaders = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + css.length + " ",
                        "",
                        "");

                // when
                processor.process(socket);

                // then
                final String actual = socket.output();
                assertThat(actual).startsWith(expectedHeaders);
                assertThat(actual.substring(expectedHeaders.length()))
                        .isEqualTo(new String(css, StandardCharsets.UTF_8));
            }
        }

        @Test
        @DisplayName("자바스크립트 파일을 올바른 Content-Type으로 응답한다")
        void javascriptFiles() throws IOException {
            // given
            final var paths = List.of(
                    "/js/scripts.js",
                    "/assets/chart-area.js",
                    "/assets/chart-bar.js",
                    "/assets/chart-pie.js");

            for (final String path : paths) {
                final var socket = new StubSocket("GET " + path + " HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
                final var processor = new Http11Processor(socket);

                try (final var resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
                    assertThat(resource).isNotNull();
                    final byte[] javascript = resource.readAllBytes();
                    final String expected = String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: text/javascript;charset=utf-8 ",
                            "Content-Length: " + javascript.length + " ",
                            "",
                            new String(javascript, StandardCharsets.UTF_8));

                    // when
                    processor.process(socket);

                    // then
                    assertThat(socket.output()).as(path).isEqualTo(expected);
                }
            }
        }
    }

    @Nested
    @DisplayName("로그인 요청")
    class LoginTests {

        @Test
        @DisplayName("쿼리 문자열 유무와 관계없이 로그인 페이지를 응답한다")
        void loginPageIsReturnedWithOrWithoutQueryString() throws IOException {
            // given
            final var requestTargets = List.of(
                    "/login",
                    "/login?account=gugu&password=password");

            try (final var resource = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
                assertThat(resource).isNotNull();
                final byte[] html = resource.readAllBytes();
                final String expected = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + html.length + " ",
                        "",
                        new String(html, StandardCharsets.UTF_8));

                for (final String requestTarget : requestTargets) {
                    final var socket = new StubSocket("GET " + requestTarget + " HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
                    final var processor = new Http11Processor(socket);

                    // when
                    processor.process(socket);

                    // then
                    assertThat(socket.output()).as(requestTarget).isEqualTo(expected);
                }
            }
        }

        @Test
        @DisplayName("계정과 비밀번호가 일치하면 사용자를 로그에 남긴다")
        void matchingLoginUserIsLogged() {
            // given
            final String requestTarget = "/login?account=gugu&password=password";

            // when
            final var messages = loginMessages(requestTarget);

            // then
            assertThat(messages)
                    .containsExactly("login user found: gugu");
        }

        @Test
        @DisplayName("쿼리 값에 등호가 포함되어도 값을 보존한다")
        void equalsSignInQueryValueIsPreserved() {
            // given
            final String requestTarget = "/login?account=gugu&password=password&note=a=b";

            // when
            final var messages = loginMessages(requestTarget);

            // then
            assertThat(messages)
                    .containsExactly("login user found: gugu");
        }

        @Test
        @DisplayName("잘못된 계정 정보나 쿼리 형식은 로그인 로그를 남기지 않는다")
        void invalidLoginUserIsNotLogged() {
            // given
            final var requestTargets = List.of(
                    "/login?account=gugu&password=wrong",
                    "/login?account=gugu&password",
                    "/login?account=gugu&password=pa=ss",
                    "/login?account=gugu&password=password&broken",
                    "/login?account=gugu&account=other&password=password");

            for (final String requestTarget : requestTargets) {
                // when
                final var messages = loginMessages(requestTarget);

                // then
                assertThat(messages).as(requestTarget).isEmpty();
            }
        }
    }

    private List<String> loginMessages(final String requestTarget) {
        final var logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);

        try {
            final var socket = new StubSocket("GET " + requestTarget + " HTTP/1.1\r\n\r\n");
            final var processor = new Http11Processor(socket);

            processor.process(socket);

            assertThat(socket.output()).startsWith("HTTP/1.1 200 OK");
            return appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .toList();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }
}
