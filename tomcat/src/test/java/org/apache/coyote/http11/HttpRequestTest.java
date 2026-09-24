package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("요청이 비어 있으면 InvalidHttpRequestException을 던진다")
    void throwsInvalidHttpRequestExceptionWhenRequestIsEmpty() {
        // given
        final BufferedReader reader = new BufferedReader(new StringReader(""));

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @Test
    @DisplayName("Content-Length가 숫자가 아니면 InvalidHttpRequestException을 던진다")
    void throwsInvalidHttpRequestExceptionWhenContentLengthIsNotNumeric() {
        // given
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: invalid",
                "",
                "account=gugu"
        );
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @Test
    @DisplayName("요청 대상 URI 형식이 잘못되면 InvalidHttpRequestException을 던진다")
    void throwsInvalidHttpRequestExceptionWhenRequestTargetIsMalformed() {
        // given
        final BufferedReader reader = new BufferedReader(new StringReader("GET /%zz HTTP/1.1\r\n\r\n"));

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @Test
    @DisplayName("요청 라인 형식이 잘못되면 InvalidHttpRequestException을 던진다")
    void throwsInvalidHttpRequestExceptionWhenRequestLineIsMalformed() {
        // given
        final BufferedReader reader = new BufferedReader(new StringReader("INVALID\r\n\r\n"));

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @Test
    @DisplayName("요청을 읽는 중 발생한 IOException은 그대로 전달한다")
    void propagatesIOExceptionWhenReadingRequestFails() {
        // given
        final IOException expected = new IOException("요청 읽기 실패");
        final BufferedReader reader = new BufferedReader(new StringReader("")) {
            @Override
            public String readLine() throws IOException {
                throw expected;
            }
        };

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isSameAs(expected);
    }

    @Test
    @DisplayName("HTTP 메서드와 경로가 모두 일치할 때만 요청이 일치한다")
    void matchesMethodAndPath() throws IOException {
        // given
        final HttpRequest request = createRequest(List.of("GET /login HTTP/1.1"), null);

        // when & then
        assertThat(request.matches("GET", "/login")).isTrue();
        assertThat(request.matches("POST", "/login")).isFalse();
        assertThat(request.matches("GET", "/register")).isFalse();
    }

    @Test
    @DisplayName("요청 헤더 이름은 대소문자를 구분하지 않고 조회한다")
    void getsHeaderIgnoringCase() throws IOException {
        // given
        final HttpRequest request = createRequest(List.of(
                "GET /index.html HTTP/1.1",
                "Content-Type: text/html;charset=utf-8"
        ), null);

        // when
        final String contentType = request.getHeader("content-type");

        // then
        assertThat(contentType).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    @DisplayName("Content-Length 헤더 이름의 대소문자와 관계없이 요청 본문을 읽는다")
    void readsBodyIgnoringContentLengthHeaderCase() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "content-length: " + body.length(),
                "",
                body
        );
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when
        final HttpRequest request = HttpRequest.from(reader, new SessionManager());

        // then
        assertThat(request.getBodyParameter("account")).isEqualTo("gugu");
        assertThat(request.getBodyParameter("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("요청 본문이 여러 번에 나뉘어 읽혀도 Content-Length만큼 누적해서 읽는다")
    void readsBodyAcrossMultipleReads() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "",
                body
        );
        final BufferedReader reader = new ChunkedBufferedReader(rawRequest, 3);

        // when
        final HttpRequest request = HttpRequest.from(reader, new SessionManager());

        // then
        assertThat(request.getBodyParameter("account")).isEqualTo("gugu");
        assertThat(request.getBodyParameter("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("큰 요청 본문이 여러 번에 나뉘어 읽혀도 잘리지 않는다")
    void readsLargeBodyAcrossMultipleReadsWithoutTruncation() throws IOException {
        // given
        final String account = "a".repeat(20_000);
        final String body = "account=" + account;
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "",
                body
        );
        final BufferedReader reader = new ChunkedBufferedReader(rawRequest, 128);

        // when
        final HttpRequest request = HttpRequest.from(reader, new SessionManager());

        // then
        assertThat(request.getBodyParameter("account"))
                .hasSize(20_000)
                .isEqualTo(account);
    }

    @Test
    @DisplayName("요청 본문을 읽을 때 Reader의 ready 상태를 폴링하지 않는다")
    void readsBodyWithoutPollingReaderReadiness() throws IOException {
        // given
        final String body = "account=gugu";
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "",
                body
        );
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest)) {
            @Override
            public boolean ready() {
                throw new AssertionError("본문을 읽기 전에 ready()를 호출하면 안 됩니다.");
            }
        };

        // when
        final HttpRequest request = HttpRequest.from(reader, new SessionManager());

        // then
        assertThat(request.getBodyParameter("account")).isEqualTo("gugu");
    }

    @Test
    @DisplayName("요청 본문이 Content-Length보다 먼저 끝나면 InvalidHttpRequestException을 던진다")
    void throwsInvalidHttpRequestExceptionWhenBodyEndsBeforeContentLength() {
        // given
        final String body = "account=gugu";
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + (body.length() + 1),
                "",
                body
        );
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @Test
    @DisplayName("요청 본문을 읽는 중 발생한 SocketTimeoutException은 그대로 전달한다")
    void propagatesSocketTimeoutExceptionWhenReadingBodyTimesOut() {
        // given
        final SocketTimeoutException expected = new SocketTimeoutException("요청 본문 읽기 시간 초과");
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: 1",
                "",
                "a"
        );
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest)) {
            @Override
            public int read(final char[] buffer, final int offset, final int length) throws IOException {
                throw expected;
            }
        };

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isSameAs(expected);
    }

    @Test
    @DisplayName("Content-Length가 음수이면 InvalidHttpRequestException을 던진다")
    void throwsInvalidHttpRequestExceptionWhenContentLengthIsNegative() {
        // given
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: -1",
                "",
                ""
        );
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @Test
    @DisplayName("Content-Length가 int 범위를 넘으면 InvalidHttpRequestException을 던진다")
    void throwsInvalidHttpRequestExceptionWhenContentLengthOverflowsInteger() {
        // given
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: 2147483648",
                "",
                ""
        );
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(reader, new SessionManager()))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @Test
    @DisplayName("URL 인코딩된 UTF-8 form 본문을 읽고 한글로 복원한다")
    void readsAndDecodesUrlEncodedUtf8FormBody() throws IOException {
        // given
        final String account = "한글";
        final String body = "account=" + URLEncoder.encode(account, StandardCharsets.UTF_8);
        final String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body
        );
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when
        final HttpRequest request = HttpRequest.from(reader, new SessionManager());

        // then
        assertThat(request.getBodyParameter("account")).isEqualTo(account);
    }

    @Test
    @DisplayName("세션이 없을 때 getSession true를 호출하면 새 세션을 등록한다")
    void createsAndRegistersSessionWhenRequested() throws IOException {
        // given
        final Manager manager = new SessionManager();
        manager.removeAll();
        final HttpRequest request = createRequest(
                List.of("GET /index.html HTTP/1.1"),
                null,
                manager
        );

        // when
        final HttpSession createdSession = request.getSession(true);

        // then
        assertThat(manager.findSession(createdSession.getId())).isSameAs(createdSession);
        assertThat(request.getSession(true)).isSameAs(createdSession);
    }

    @Test
    @DisplayName("세션이 없을 때 getSession false를 호출하면 세션을 생성하지 않는다")
    void doesNotCreateSessionWhenNotRequested() throws IOException {
        // given
        final Manager manager = new SessionManager();
        manager.removeAll();
        final HttpRequest request = createRequest(
                List.of("GET /index.html HTTP/1.1"),
                null,
                manager
        );

        // when
        final HttpSession session = request.getSession(false);

        // then
        assertThat(session).isNull();
    }

    @Test
    @DisplayName("Cookie의 JSESSIONID로 기존 세션을 조회한다")
    void findsSessionByJSessionIdCookie() throws IOException {
        // given
        final Manager manager = new SessionManager();
        manager.removeAll();
        final HttpSession session = new Session("request-session-id");
        manager.add(session);

        final List<String> headers = List.of(
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=request-session-id"
        );

        // when
        final HttpRequest request = createRequest(headers, null, manager);

        // then
        assertThat(request.getSession()).isSameAs(session);
    }

    @Test
    @DisplayName("요청 헤더의 Cookie를 파싱한다")
    void parsesCookieHeader() throws IOException {
        // given
        final List<String> headers = List.of(
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; JSESSIONID=abc-123"
        );

        // when
        final HttpRequest request = createRequest(headers, null);

        // then
        assertThat(request.getCookie().get("JSESSIONID")).contains("abc-123");
    }

    @Test
    @DisplayName("요청 라인에서 HTTP 메서드와 경로를 분리한다")
    void parsesMethodAndPathFromRequestLine() throws IOException {
        final HttpRequest request = createRequest(List.of("GET /index.html HTTP/1.1"), null);

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("URI의 쿼리 문자열을 파라미터로 분리한다")
    void parsesQueryStringParameters() throws IOException {
        final HttpRequest request = createRequest(List.of(
                "GET /login?account=gugu&password=password HTTP/1.1"), null);

        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("쿼리 문자열이 없으면 파라미터는 null이다")
    void returnsNullWhenQueryStringIsMissing() throws IOException {
        final HttpRequest request = createRequest(List.of("GET /login HTTP/1.1"), null);

        assertThat(request.getParameter("account")).isNull();
    }

    @Test
    @DisplayName("쿼리 문자열의 인코딩된 문자를 디코딩한다")
    void decodesEncodedQueryString() throws IOException {
        final HttpRequest request = createRequest(List.of(
                "GET /login?account=gugu%40email.com&password=pass%20word HTTP/1.1"
        ), null);

        assertThat(request.getParameter("account")).isEqualTo("gugu@email.com");
        assertThat(request.getParameter("password")).isEqualTo("pass word");
    }

    @Test
    @DisplayName("요청 본문의 파라미터를 분리한다")
    void parsesBodyParameters() throws IOException {
        final String body = "account=tion&email=ehfrhfo9494@naver.com&password=password";
        final HttpRequest request = createRequest(List.of(
                "POST /login?account=gugu&password=password HTTP/1.1",
                "Content-Length: " + body.length()
        ), body);

        assertThat(request.getBodyParameter("account")).isEqualTo("tion");
    }

    private static HttpRequest createRequest(final List<String> headerLines,
                                             final String body) throws IOException {
        return createRequest(headerLines, body, new SessionManager());
    }

    private static HttpRequest createRequest(final List<String> headerLines,
                                             final String body,
                                             final Manager manager) throws IOException {
        final String rawRequest = String.join("\r\n", headerLines)
                + "\r\n\r\n"
                + (body == null ? "" : body);
        final BufferedReader reader = new BufferedReader(new StringReader(rawRequest));
        return HttpRequest.from(reader, manager);
    }

    private static final class ChunkedBufferedReader extends BufferedReader {

        private final int chunkSize;

        private ChunkedBufferedReader(final String source, final int chunkSize) {
            super(new StringReader(source));
            this.chunkSize = chunkSize;
        }

        @Override
        public int read(final char[] buffer, final int offset, final int length) throws IOException {
            return super.read(buffer, offset, Math.min(length, chunkSize));
        }
    }
}
