package org.apache.coyote.http11;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import support.StubSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
    @DisplayName("요청 처리 실패")
    class FailureTests {

        @Test
        @DisplayName("요청을 읽지 못하면 읽기 실패를 기록한다")
        void logsRequestReadFailure() {
            // given
            final InputStream inputStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("request read failed");
                }
            };
            final var socket = new StubSocket() {
                @Override
                public InputStream getInputStream() {
                    return inputStream;
                }
            };
            final var processor = new Http11Processor(socket);

            // when
            final var events = processorLogs(() -> processor.process(socket));

            // then
            assertThat(events)
                    .extracting(event -> event.getLevel() + ": " + event.getFormattedMessage())
                    .containsExactly("WARN: Failed to read HTTP request");
        }

        @Test
        @DisplayName("정적 리소스 준비 실패 시 500 상태를 응답한다")
        void resourceFailureReturnsServerErrorStatus() {
            // given
            final var socket = new StubSocket("GET /index.html HTTP/1.1\r\n\r\n");
            final var resolver = mock(ResponseContentResolver.class);
            when(resolver.resolve("/index.html")).thenThrow(resourceLoadingFailure());
            final var processor = new Http11Processor(socket, resolver);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).startsWith("HTTP/1.1 500 Internal Server Error ");
        }

        @Test
        @DisplayName("정적 리소스 준비 실패 시 내부 정보를 노출하지 않는다")
        void resourceFailureReturnsSafeErrorBody() {
            // given
            final var socket = new StubSocket("GET /index.html HTTP/1.1\r\n\r\n");
            final var resolver = mock(ResponseContentResolver.class);
            when(resolver.resolve("/index.html")).thenThrow(resourceLoadingFailure());
            final var processor = new Http11Processor(socket, resolver);

            // when
            processor.process(socket);

            // then
            assertThat(responseBody(socket.output())).isEqualTo("Internal Server Error");
        }

        @Test
        @DisplayName("정적 리소스 읽기 실패를 요청 경로와 함께 기록한다")
        void logsResourceReadFailureWithPath() {
            // given
            final var socket = new StubSocket("GET /index.html HTTP/1.1\r\n\r\n");
            final var resolver = mock(ResponseContentResolver.class);
            when(resolver.resolve("/index.html")).thenThrow(resourceLoadingFailure());
            final var processor = new Http11Processor(socket, resolver);

            // when
            final var events = processorLogs(() -> processor.process(socket));

            // then
            assertThat(events)
                    .extracting(event -> event.getLevel() + ": " + event.getFormattedMessage())
                    .containsExactly("ERROR: Failed to load resource for path: /index.html");
        }

        @Test
        @DisplayName("HTTP 응답 전송 실패를 기록한다")
        void logsResponseWriteFailure() throws IOException {
            // given
            final OutputStream outputStream = mock(OutputStream.class);
            doThrow(new IOException("client disconnected")).when(outputStream).write(any(byte[].class));
            final var socket = new StubSocket("GET /index.html HTTP/1.1\r\n\r\n") {
                @Override
                public OutputStream getOutputStream() {
                    return outputStream;
                }
            };
            final var resolver = mock(ResponseContentResolver.class);
            when(resolver.resolve("/index.html"))
                    .thenReturn(new ResponseContent("text/html;charset=utf-8", new byte[0]));
            final var processor = new Http11Processor(socket, resolver);

            // when
            final var events = processorLogs(() -> processor.process(socket));

            // then
            assertThat(events)
                    .extracting(event -> event.getLevel() + ": " + event.getFormattedMessage())
                    .containsExactly("WARN: Failed to write HTTP response");
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

        @Test
        @DisplayName("이름과 값으로 구성되지 않은 헤더가 있으면 응답하지 않는다")
        void malformedHeaderIsRejected() {
            // given
            final var socket = new StubSocket(
                    "GET /index.html HTTP/1.1\r\nContent-Length 80\r\n\r\n");
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).isEmpty();
        }

        @Test
        @DisplayName("잘못된 URI 형식의 요청 대상에는 응답하지 않는다")
        void invalidRequestTargetIsRejected() {
            // given
            final var requestTarget = "/login?account=%ZZ";

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(response).isEmpty();
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

        @ParameterizedTest(name = "{displayName} | 입력: {0}")
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

        @ParameterizedTest(name = "{displayName} | 입력: {0}")
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

        @Test
        @DisplayName("로그인 페이지 요청에 로그인 페이지를 응답한다")
        void loginPageIsReturned() throws IOException {
            // given
            final var requestTarget = "/login";
            final var expectedBody = new String(readResource("static/login.html"), StandardCharsets.UTF_8);

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(responseBody(response)).isEqualTo(expectedBody);
        }

        @Test
        @DisplayName("로그인 폼은 POST 방식으로 제출한다")
        void loginFormUsesPostMethod() {
            // given
            final var requestTarget = "/login";

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(responseBody(response)).contains("<form method=\"post\" action=\"login\">");
        }

        @ParameterizedTest(name = "{displayName} | 요청: {0}")
        @ValueSource(strings = {
                "account=gugu&password=password",
                "account=gugu&password=wrong"
        })
        @DisplayName("로그인을 시도하면 302 Found 상태를 응답한다")
        void loginAttemptReturnsFoundStatus(final String requestBody) {
            // given: 각 요청 본문은 @ValueSource에서 전달된다.

            // when
            final var response = postResponseTo("/login", requestBody);

            // then
            assertThat(response).startsWith("HTTP/1.1 302 Found ");
        }

        @ParameterizedTest(name = "{displayName} | 요청: {0}, 이동 경로: {1}")
        @CsvSource({
                "account=gugu&password=password, /index.html",
                "account=gugu&password=wrong, /401.html"
        })
        @DisplayName("로그인 결과에 맞는 경로를 Location 헤더에 응답한다")
        void loginAttemptRedirectsToExpectedLocation(final String requestBody, final String expectedLocation) {
            // given: 요청 본문과 예상 이동 경로는 @CsvSource에서 전달된다.

            // when
            final var response = postResponseTo("/login", requestBody);

            // then
            assertThat(response).contains("\r\nLocation: " + expectedLocation + " \r\n");
        }

        @Test
        @DisplayName("계정과 비밀번호가 일치하면 사용자를 로그에 남긴다")
        void matchingLoginUserIsLogged() {
            // given
            final var requestBody = "account=gugu&password=password";

            // when
            final var messages = loginMessages(requestBody);

            // then
            assertThat(messages)
                    .containsExactly("login user found: gugu");
        }

        @Test
        @DisplayName("추가 본문 값에 등호가 있어도 로그인할 수 있다")
        void equalsSignInAdditionalBodyValueDoesNotPreventLogin() {
            // given
            final var requestBody = "account=gugu&password=password&note=a=b";

            // when
            final var messages = loginMessages(requestBody);

            // then
            assertThat(messages)
                    .containsExactly("login user found: gugu");
        }

        @Test
        @DisplayName("추가 본문 값의 인코딩된 앰퍼샌드는 파라미터 구분자로 취급하지 않는다")
        void encodedAmpersandInAdditionalBodyValueDoesNotPreventLogin() {
            // given
            final var requestBody = "note=a%26b&account=gugu&password=password";

            // when
            final var messages = loginMessages(requestBody);

            // then
            assertThat(messages).containsExactly("login user found: gugu");
        }

        @ParameterizedTest(name = "{displayName} | 입력: {0}")
        @ValueSource(strings = {
                "account=gugu&password=wrong",
                "account=gugu&password=pa=ss"
        })
        @DisplayName("비밀번호가 일치하지 않으면 로그인 로그를 남기지 않는다")
        void mismatchedPasswordIsNotLogged(final String requestBody) {
            // given: 각 요청 본문은 @ValueSource에서 전달된다.

            // when
            final var messages = loginMessages(requestBody);

            // then
            assertThat(messages).isEmpty();
        }

        @ParameterizedTest(name = "{displayName} | 입력: {0}")
        @ValueSource(strings = {
                "account=gugu&password",
                "account=gugu&password=password&broken"
        })
        @DisplayName("이름과 값으로 구성되지 않은 본문에는 로그인 로그를 남기지 않는다")
        void malformedBodyParameterIsNotLogged(final String requestBody) {
            // given: 각 요청 본문은 @ValueSource에서 전달된다.

            // when
            final var messages = loginMessages(requestBody);

            // then
            assertThat(messages).isEmpty();
        }

        @Test
        @DisplayName("중복된 계정 이름의 마지막 값이 다르면 로그인 로그를 남기지 않는다")
        void duplicatedAccountParameterWithWrongLastValueIsNotLogged() {
            // given
            final var requestBody = "account=gugu&account=other&password=password";

            // when
            final var messages = loginMessages(requestBody);

            // then
            assertThat(messages).isEmpty();
        }
    }

    @Nested
    @DisplayName("회원가입 요청")
    class RegistrationTests {

        @Test
        @DisplayName("GET /register 요청에 회원가입 페이지를 응답한다")
        void registerPageIsReturned() throws IOException {
            // given
            final var expectedBody = new String(readResource("static/register.html"), StandardCharsets.UTF_8);

            // when
            final var response = responseTo("/register");

            // then
            assertThat(responseBody(response)).isEqualTo(expectedBody);
        }

        @Test
        @DisplayName("회원가입에 성공하면 302 Found 상태를 응답한다")
        void successfulRegistrationReturnsFoundStatus() {
            // given
            final var body = "account=new-user&password=secret&email=new-user%40example.com";

            // when
            final var response = postResponseTo("/register", body);

            // then
            assertThat(response).startsWith("HTTP/1.1 302 Found ");
        }

        @Test
        @DisplayName("회원가입에 성공하면 인덱스 페이지로 이동시킨다")
        void successfulRegistrationRedirectsToIndex() {
            // given
            final var body = "account=redirect-user&password=secret&email=redirect%40example.com";

            // when
            final var response = postResponseTo("/register", body);

            // then
            assertThat(response).contains("\r\nLocation: /index.html \r\n");
        }

        @Test
        @DisplayName("회원가입 요청의 사용자를 저장한다")
        void registrationSavesUser() {
            // given
            final var account = "saved-user";
            final var body = "account=" + account + "&password=secret&email=saved%40example.com";

            // when
            postResponseTo("/register", body);

            // then
            assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
        }

        @Test
        @DisplayName("필수 회원 정보가 없으면 400 Bad Request 상태를 응답한다")
        void missingRegistrationParameterReturnsBadRequest() {
            // given
            final var body = "account=incomplete-user&password=secret";

            // when
            final var response = postResponseTo("/register", body);

            // then
            assertThat(response).startsWith("HTTP/1.1 400 Bad Request ");
        }
    }

    @Nested
    @DisplayName("세션 쿠키")
    class SessionCookieTests {

        @Test
        @DisplayName("일반 요청에 JSESSIONID가 없어도 새 세션 쿠키를 응답하지 않는다")
        void missingSessionCookieDoesNotAddSetCookieHeader() {
            // given
            final var requestTarget = "/index.html";

            // when
            final var response = responseTo(requestTarget);

            // then
            assertThat(response).doesNotContain("\r\nSet-Cookie:");
        }

        @Test
        @DisplayName("일반 요청에 다른 쿠키만 있어도 새 세션 쿠키를 응답하지 않는다")
        void unrelatedCookieDoesNotAddSetCookieHeader() {
            // given
            final var requestTarget = "/index.html";
            final var cookieHeader = "Cookie: yummy_cookie=choco";

            // when
            final var response = responseTo(requestTarget, cookieHeader);

            // then
            assertThat(response).doesNotContain("\r\nSet-Cookie:");
        }

        @Test
        @DisplayName("요청에 JSESSIONID가 있으면 새 세션 쿠키를 응답하지 않는다")
        void existingSessionCookieDoesNotAddSetCookieHeader() {
            // given
            final var requestTarget = "/index.html";
            final var session = SessionManager.getInstance().createSession();
            final var cookieHeader = "Cookie: yummy_cookie=choco; JSESSIONID=" + session.getId();

            // when
            final var response = responseTo(requestTarget, cookieHeader);

            // then
            assertThat(response).doesNotContain("\r\nSet-Cookie:");
        }

        @Test
        @DisplayName("일반 요청에 존재하지 않는 JSESSIONID가 있어도 새 세션 쿠키를 응답하지 않는다")
        void unknownSessionCookieDoesNotAddNewSetCookieHeader() {
            // given
            final var requestTarget = "/index.html";
            final var cookieHeader = "Cookie: JSESSIONID=unknown-session";

            // when
            final var response = responseTo(requestTarget, cookieHeader);

            // then
            assertThat(response).doesNotContain("\r\nSet-Cookie:");
        }
    }

    @Nested
    @DisplayName("로그인 세션")
    class LoginSessionTests {

        @Test
        @DisplayName("세션 없이 로그인에 성공하면 새 세션 쿠키를 응답한다")
        void successfulLoginWithoutSessionAddsSetCookieHeader() {
            // given
            final var requestBody = "account=gugu&password=password";

            // when
            final var response = postResponseTo("/login", requestBody);

            // then
            assertThat(response).containsPattern("\\r\\nSet-Cookie: JSESSIONID=[0-9a-f-]{36} \\r\\n");
        }

        @Test
        @DisplayName("세션 없이 로그인에 실패하면 새 세션 쿠키를 응답하지 않는다")
        void failedLoginWithoutSessionDoesNotAddSetCookieHeader() {
            // given
            final var requestBody = "account=gugu&password=wrong";

            // when
            final var response = postResponseTo("/login", requestBody);

            // then
            assertThat(response).doesNotContain("\r\nSet-Cookie:");
        }

        @Test
        @DisplayName("로그인에 성공하면 세션에 사용자를 저장한다")
        void successfulLoginStoresUserInSession() {
            // given
            final var session = SessionManager.getInstance().createSession();
            final var cookieHeader = "Cookie: JSESSIONID=" + session.getId();
            final var requestBody = "account=gugu&password=password";

            // when
            postResponseTo("/login", requestBody, cookieHeader);

            // then
            assertThat(session.getAttribute("user")).isInstanceOf(User.class);
        }

        @Test
        @DisplayName("로그인된 사용자가 로그인 페이지를 요청하면 302 Found 상태를 응답한다")
        void loggedInUserRequestingLoginPageReturnsFoundStatus() {
            // given
            final var cookieHeader = loggedInSessionCookieHeader();

            // when
            final var response = responseTo("/login", cookieHeader);

            // then
            assertThat(response).startsWith("HTTP/1.1 302 Found ");
        }

        @Test
        @DisplayName("로그인된 사용자가 로그인 페이지를 요청하면 인덱스 페이지로 이동시킨다")
        void loggedInUserRequestingLoginPageRedirectsToIndex() {
            // given
            final var cookieHeader = loggedInSessionCookieHeader();

            // when
            final var response = responseTo("/login", cookieHeader);

            // then
            assertThat(response).contains("\r\nLocation: /index.html \r\n");
        }
    }

    private String responseTo(final String requestTarget) {
        final var socket = new StubSocket("GET " + requestTarget + " HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket);
        processor.process(socket);
        return socket.output();
    }

    private String responseTo(final String requestTarget, final String header) {
        final var request = String.join("\r\n",
                "GET " + requestTarget + " HTTP/1.1",
                "Host: localhost:8080",
                header,
                "",
                "");
        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);
        processor.process(socket);
        return socket.output();
    }

    private String postResponseTo(final String requestTarget, final String body) {
        final var request = String.join("\r\n",
                "POST " + requestTarget + " HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body);
        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);
        processor.process(socket);
        return socket.output();
    }

    private String postResponseTo(final String requestTarget, final String body, final String header) {
        final var request = String.join("\r\n",
                "POST " + requestTarget + " HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                header,
                "",
                body);
        final var socket = new StubSocket(request);
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

    private List<String> loginMessages(final String requestBody) {
        final var logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);

        try {
            postResponseTo("/login", requestBody);

            return appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .toList();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    private String loggedInSessionCookieHeader() {
        final var session = SessionManager.getInstance().createSession();
        session.setAttribute("user", new User("gugu", "password", "gugu@example.com"));
        return "Cookie: JSESSIONID=" + session.getId();
    }

    private List<ILoggingEvent> processorLogs(final Runnable action) {
        final var logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);

        try {
            action.run();
            return List.copyOf(appender.list);
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    private HttpException resourceLoadingFailure() {
        return new HttpException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to load resource for path: /index.html", new IOException("resource unavailable"));
    }
}
