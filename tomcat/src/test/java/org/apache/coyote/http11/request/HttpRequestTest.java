package org.apache.coyote.http11.request;

import org.apache.coyote.http11.BadRequestException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {
    private final SessionManager sessionManager = new SessionManager();

    @Test
    void parseRequestLine() {
        // given
        final String requestLine = "GET /login?account=gugu HTTP/1.1";

        // when
        final HttpRequest request = request(requestLine, HttpHeaders.empty(), HttpBody.empty());

        // then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHttpPath()).isEqualTo("/login");
        assertThat(request.getQueryParams("account")).isEqualTo("gugu");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void invalidRequestLine() {
        // given
        final String requestLine = "GET /index.html ABC HTTP/1.1";

        // when & then
        assertThatThrownBy(() -> request(requestLine, HttpHeaders.empty(), HttpBody.empty()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 http요청 형태입니다.");
    }

    @Test
    void supportedMethod() {
        // given
        final String requestLine = "GET /index.html HTTP/1.1";
        final Set<HttpMethod> supportedMethods = Set.of(HttpMethod.GET);

        // when
        final HttpRequest request = request(requestLine, HttpHeaders.empty(), HttpBody.empty(), supportedMethods);

        // then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHttpPath()).isEqualTo("/index.html");
    }

    @Test
    void unsupportedMethod() {
        // given
        final String requestLine = "POST /index.html HTTP/1.1";
        final Set<HttpMethod> supportedMethods = Set.of(HttpMethod.GET);

        // when & then
        assertThatThrownBy(() -> request(requestLine, HttpHeaders.empty(), HttpBody.empty(), supportedMethods))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("지원하지 않는 HTTP 메서드입니다: POST");
    }

    @Test
    void withoutSupportedMethods() {
        // given
        final String requestLine = "POST /index.html HTTP/1.1";

        // when
        final HttpRequest request = request(requestLine, HttpHeaders.empty(), HttpBody.empty());

        // then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
    }

    @Test
    void unsupportedVersion() {
        // given
        final String requestLine = "GET /index.html ABC";

        // when & then
        assertThatThrownBy(() -> request(requestLine, HttpHeaders.empty(), HttpBody.empty()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("지원하지 않는 HTTP 버전입니다: ABC");
    }

    @Test
    void requestWithHeadersAndBody() {
        // given
        final String requestLine = "GET /login HTTP/1.1";
        final HttpHeaders headers = HttpHeaders.from(List.of("Host: localhost:8080", "Content-Length: 12"));
        final HttpBody body = new HttpBody("account=gugu");

        // when
        final HttpRequest request = request(requestLine, headers, body);

        // then
        assertThat(request.getHeader("host")).isEqualTo("localhost:8080");
        assertThat(request.getBody().getContent()).isEqualTo("account=gugu");
    }

    @Test
    void formBodyParams() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded"));
        final HttpBody body = new HttpBody("account=gugu2&email=gugu2%40woowahan.com");

        // when
        final HttpRequest request = request("POST /register HTTP/1.1", headers, body);

        // then
        assertThat(request.getBodyParams("account")).isEqualTo("gugu2");
        assertThat(request.getBodyParams("email")).isEqualTo("gugu2@woowahan.com");
    }

    @Test
    void formBodyParamsWithCharset() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded; charset=UTF-8"));
        final HttpBody body = new HttpBody("account=gugu2");

        // when
        final HttpRequest request = request("POST /register HTTP/1.1", headers, body);

        // then
        assertThat(request.getBodyParams("account")).isEqualTo("gugu2");
    }

    @Test
    void queryParamsAndBodyParamsAreSeparated() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded"));
        final HttpBody body = new HttpBody("account=fromBody");

        // when
        final HttpRequest request = request("POST /register?account=fromQuery HTTP/1.1", headers, body);

        // then
        assertThat(request.getQueryParams("account")).isEqualTo("fromQuery");
        assertThat(request.getBodyParams("account")).isEqualTo("fromBody");
    }

    @Test
    void bodyIsNotParsedWhenNotForm() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Type: application/json"));
        final HttpBody body = new HttpBody("account=gugu2");

        // when
        final HttpRequest request = request("POST /register HTTP/1.1", headers, body);

        // then
        assertThat(request.getBodyParams("account")).isNull();
    }

    @Test
    void bodyIsNotParsedWhenNotPost() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded"));
        final HttpBody body = new HttpBody("account=gugu2");

        // when
        final HttpRequest request = request("GET /register HTTP/1.1", headers, body);

        // then
        assertThat(request.getBodyParams("account")).isNull();
    }

    @Test
    void invalidFormBody() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Content-Type: application/x-www-form-urlencoded"));
        final HttpBody body = new HttpBody("account=%");

        // when & then
        assertThatThrownBy(() -> request("POST /register HTTP/1.1", headers, body))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("잘못된 쿼리 스트링입니다.");
    }

    @Test
    void cookieFromHeader() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Cookie: yummy_cookie=choco; JSESSIONID=abc"));

        // when
        final HttpRequest request = request("GET /index.html HTTP/1.1", headers, HttpBody.empty());

        // then
        assertThat(request.getCookie().get("JSESSIONID")).isEqualTo("abc");
    }

    @Test
    void cookieIsParsedOnce() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Cookie: JSESSIONID=abc"));
        final HttpRequest request = request("GET /index.html HTTP/1.1", headers, HttpBody.empty());

        // when
        request.getCookie().add("theme", "dark");

        // then
        assertThat(request.getCookie()).isSameAs(request.getCookie());
        assertThat(request.getCookie().get("theme")).isEqualTo("dark");
        assertThat(request.getCookie().get("JSESSIONID")).isEqualTo("abc");
    }

    @Test
    void noSessionWithoutCookie() {
        // given
        final HttpRequest request = request("GET /index.html HTTP/1.1", HttpHeaders.empty(), HttpBody.empty());

        // when & then
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    void createSessionWhenRequested() {
        // given
        final HttpRequest request = request("GET /index.html HTTP/1.1", HttpHeaders.empty(), HttpBody.empty());

        // when
        final Session session = request.getSession(true);

        // then
        assertThat(session.getId()).matches("[0-9a-f-]{36}");
        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
        assertThat(request.getSession(false)).isSameAs(session);
    }

    @Test
    void findSessionByJSessionIdCookie() {
        // given
        final Session session = new Session("request-existing-session");
        sessionManager.add(session);
        final HttpHeaders headers = HttpHeaders.from(List.of("Cookie: JSESSIONID=request-existing-session"));

        // when
        final HttpRequest request = request("GET /index.html HTTP/1.1", headers, HttpBody.empty());

        // then
        assertThat(request.getSession(false)).isSameAs(session);
        assertThat(request.getSession(true)).isSameAs(session);
    }

    @Test
    void unknownSessionIdIsNotFound() {
        // given
        final HttpHeaders headers = HttpHeaders.from(List.of("Cookie: JSESSIONID=request-unknown-session"));

        // when
        final HttpRequest request = request("GET /index.html HTTP/1.1", headers, HttpBody.empty());

        // then
        assertThat(request.getSession(false)).isNull();
    }
    private HttpRequest request(String requestLine, HttpHeaders headers, HttpBody body) {
        return HttpRequest.from(requestLine, headers, body, sessionManager);
    }

    private HttpRequest request(
            String requestLine,
            HttpHeaders headers,
            HttpBody body,
            Set<HttpMethod> supportedMethods
    ) {
        return HttpRequest.from(requestLine, headers, body, supportedMethods, sessionManager);
    }

}
