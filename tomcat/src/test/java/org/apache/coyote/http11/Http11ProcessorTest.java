package org.apache.coyote.http11;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import support.StubSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

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
        @DisplayName("루트 경로에 200 OK 상태를 응답한다")
        void rootReturnsOkStatus() {
            // given
            final var requestTarget = "/";

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(response).startsWith("HTTP/1.1 200 OK ");
        }

        @Test
        @DisplayName("루트 경로에 Hello world! 본문을 응답한다")
        void rootReturnsHelloWorldBody() {
            // given
            final var requestTarget = "/";

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(responseBody(response)).isEqualTo("Hello world!");
        }

        @Test
        @DisplayName("주입한 응답 내용 결정자에게 요청 경로를 전달한다")
        void passesRequestPathToInjectedResolver() throws IOException {
            // given
            final var socket = new StubSocket("GET /custom HTTP/1.1\r\n\r\n");
            final var resolver = mock(ResponseContentResolver.class);
            when(resolver.resolve("/custom"))
                    .thenReturn(new ResponseContent("text/plain;charset=utf-8", new byte[0]));
            final var processor = new Http11Processor(socket, resolver);

            // when
            processor.process(socket);

            // then
            verify(resolver).resolve("/custom");
        }

        @Test
        @DisplayName("주입한 응답 내용의 Content-Type을 응답 헤더에 쓴다")
        void writesInjectedContentType() throws IOException {
            // given
            final var socket = new StubSocket("GET /custom HTTP/1.1\r\n\r\n");
            final var resolver = mock(ResponseContentResolver.class);
            when(resolver.resolve("/custom"))
                    .thenReturn(new ResponseContent("text/plain;charset=utf-8", new byte[0]));
            final var processor = new Http11Processor(socket, resolver);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("Content-Type: text/plain;charset=utf-8 ");
        }

        @Test
        @DisplayName("주입한 응답 내용을 본문에 쓴다")
        void writesInjectedBody() throws IOException {
            // given
            final var socket = new StubSocket("GET /custom HTTP/1.1\r\n\r\n");
            final var resolver = mock(ResponseContentResolver.class);
            when(resolver.resolve("/custom"))
                    .thenReturn(new ResponseContent("text/plain;charset=utf-8", "custom".getBytes(StandardCharsets.UTF_8)));
            final var processor = new Http11Processor(socket, resolver);

            // when
            processor.process(socket);

            // then
            assertThat(responseBody(socket.output())).isEqualTo("custom");
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
        @DisplayName("빈 요청에는 응답하지 않는다")
        void emptyRequestIsIgnored() {
            // given
            final var socket = new StubSocket("");
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).isEmpty();
        }

        @Test
        @DisplayName("헤더가 빈 줄로 끝나지 않으면 응답하지 않는다")
        void unterminatedHeadersAreIgnored() {
            // given
            final var socket = new StubSocket("GET /index.html HTTP/1.1\r\nHost: localhost:8080");
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).isEmpty();
        }
    }

    @Nested
    @DisplayName("정적 리소스 응답")
    class StaticResourceTests {

        @Test
        @DisplayName("인덱스 페이지의 Content-Type은 HTML이다")
        void indexContentTypeIsHtml() {
            // given
            final var requestTarget = "/index.html";

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(response).contains("\r\nContent-Type: text/html;charset=utf-8 \r\n");
        }

        @Test
        @DisplayName("인덱스 페이지의 Content-Length는 본문 바이트 수이다")
        void indexContentLengthMatchesBodyBytes() throws IOException {
            // given
            final var requestTarget = "/index.html";
            final var expectedLength = readResource("static/index.html").length;

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(response).contains("\r\nContent-Length: " + expectedLength + " \r\n");
        }

        @Test
        @DisplayName("인덱스 페이지의 본문은 index.html의 내용이다")
        void indexBodyMatchesResource() throws IOException {
            // given
            final var requestTarget = "/index.html";
            final var expectedBody = new String(readResource("static/index.html"), StandardCharsets.UTF_8);

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(responseBody(response)).isEqualTo(expectedBody);
        }

        @Test
        @DisplayName("스타일시트의 Content-Type은 CSS이다")
        void cssContentTypeIsCss() {
            // given
            final var requestTarget = "/css/styles.css";

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(response).contains("\r\nContent-Type: text/css;charset=utf-8 \r\n");
        }

        @Test
        @DisplayName("스타일시트의 본문은 styles.css의 내용이다")
        void cssBodyMatchesResource() throws IOException {
            // given
            final var requestTarget = "/css/styles.css";
            final var expectedBody = new String(readResource("static/css/styles.css"), StandardCharsets.UTF_8);

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(responseBody(response)).isEqualTo(expectedBody);
        }

        @ParameterizedTest(name = "요청 경로: {0}")
        @ValueSource(strings = {
                "/js/scripts.js",
                "/assets/chart-area.js",
                "/assets/chart-bar.js",
                "/assets/chart-pie.js"
        })
        @DisplayName("자바스크립트 파일의 Content-Type은 JavaScript이다")
        void javascriptContentTypeIsJavaScript(final String requestTarget) {
            // given
            final var expectedContentType = "text/javascript;charset=utf-8";

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(response).contains("\r\nContent-Type: " + expectedContentType + " \r\n");
        }

        @ParameterizedTest(name = "요청 경로: {0}")
        @ValueSource(strings = {
                "/js/scripts.js",
                "/assets/chart-area.js",
                "/assets/chart-bar.js",
                "/assets/chart-pie.js"
        })
        @DisplayName("자바스크립트 파일의 본문은 해당 파일의 내용이다")
        void javascriptBodyMatchesResource(final String requestTarget) throws IOException {
            // given
            final var expectedBody = new String(readResource("static" + requestTarget), StandardCharsets.UTF_8);

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(responseBody(response)).isEqualTo(expectedBody);
        }
    }

    @Nested
    @DisplayName("로그인 요청")
    class LoginTests {

        @ParameterizedTest(name = "요청 경로: {0}")
        @ValueSource(strings = {
                "/login",
                "/login?account=gugu&password=password"
        })
        @DisplayName("로그인 요청에는 쿼리 유무와 관계없이 로그인 페이지를 응답한다")
        void loginPageIsReturnedWithOrWithoutQueryString(final String requestTarget) throws IOException {
            // given
            final var expectedBody = new String(readResource("static/login.html"), StandardCharsets.UTF_8);

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(responseBody(response)).isEqualTo(expectedBody);
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
        @DisplayName("추가 쿼리 값에 등호가 있어도 로그인할 수 있다")
        void equalsSignInAdditionalQueryValueDoesNotPreventLogin() {
            // given
            final String requestTarget = "/login?account=gugu&password=password&note=a=b";

            // when
            final var messages = loginMessages(requestTarget);

            // then
            assertThat(messages)
                    .containsExactly("login user found: gugu");
        }

        @ParameterizedTest(name = "요청 경로: {0}")
        @ValueSource(strings = {
                "/login?account=gugu&password=wrong",
                "/login?account=gugu&password=pa=ss"
        })
        @DisplayName("비밀번호가 일치하지 않으면 로그인 로그를 남기지 않는다")
        void mismatchedPasswordIsNotLogged(final String requestTarget) {
            // given: 각 요청 경로는 @ValueSource에서 전달된다.

            // when
            final var messages = loginMessages(requestTarget);

            // then
            assertThat(messages).isEmpty();
        }

        @ParameterizedTest(name = "요청 경로: {0}")
        @ValueSource(strings = {
                "/login?account=gugu&password",
                "/login?account=gugu&password=password&broken"
        })
        @DisplayName("이름과 값으로 구성되지 않은 쿼리에는 로그인 로그를 남기지 않는다")
        void malformedQueryParameterIsNotLogged(final String requestTarget) {
            // given: 각 요청 경로는 @ValueSource에서 전달된다.

            // when
            final var messages = loginMessages(requestTarget);

            // then
            assertThat(messages).isEmpty();
        }

        @Test
        @DisplayName("중복된 계정 이름의 마지막 값이 다르면 로그인 로그를 남기지 않는다")
        void duplicatedAccountParameterWithWrongLastValueIsNotLogged() {
            // given
            final var requestTarget = "/login?account=gugu&account=other&password=password";

            // when
            final var messages = loginMessages(requestTarget);

            // then
            assertThat(messages).isEmpty();
        }
    }

    private String responseTo(final String requestTarget) {
        final var socket = new StubSocket("GET " + requestTarget + " HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket);
        processor.process(socket);
        return socket.output();
    }

    private String responseBody(final String response) {
        final var separator = "\r\n\r\n";
        final int separatorIndex = response.indexOf(separator);
        if (separatorIndex < 0) {
            throw new AssertionError("HTTP 응답에 헤더와 본문을 구분하는 빈 줄이 없습니다");
        }
        return response.substring(separatorIndex + separator.length());
    }

    private byte[] readResource(final String resourcePath) throws IOException {
        try (final var resource = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(resourcePath),
                "테스트 리소스가 없습니다: " + resourcePath)) {
            return resource.readAllBytes();
        }
    }

    private List<String> loginMessages(final String requestTarget) {
        final var logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);

        try {
            responseTo(requestTarget);

            return appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .toList();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }
}
