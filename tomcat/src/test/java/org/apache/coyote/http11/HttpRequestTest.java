package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("HTTP 메서드와 경로가 모두 일치할 때만 요청이 일치한다")
    void matchesMethodAndPath() {
        // given
        final HttpRequest request = HttpRequest.of(List.of("GET /login HTTP/1.1"), null);

        // when & then
        assertThat(request.matches("GET", "/login")).isTrue();
        assertThat(request.matches("POST", "/login")).isFalse();
        assertThat(request.matches("GET", "/register")).isFalse();
    }

    @Test
    @DisplayName("요청 헤더 이름은 대소문자를 구분하지 않고 조회한다")
    void getsHeaderIgnoringCase() {
        // given
        final HttpRequest request = HttpRequest.of(List.of(
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
    void readsBodyIgnoringContentLengthHeaderCase() {
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
    @DisplayName("세션이 없을 때 getSession true를 호출하면 새 세션을 등록한다")
    void createsAndRegistersSessionWhenRequested() throws IOException {
        // given
        final Manager manager = new SessionManager();
        manager.removeAll();
        final HttpRequest request = HttpRequest.of(
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
    void doesNotCreateSessionWhenNotRequested() {
        // given
        final Manager manager = new SessionManager();
        manager.removeAll();
        final HttpRequest request = HttpRequest.of(
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
    void findsSessionByJSessionIdCookie() {
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
        final HttpRequest request = HttpRequest.of(headers, null, manager);

        // then
        assertThat(request.getSession()).isSameAs(session);
    }

    @Test
    @DisplayName("요청 헤더의 Cookie를 파싱한다")
    void parsesCookieHeader() {
        // given
        final List<String> headers = List.of(
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; JSESSIONID=abc-123"
        );

        // when
        final HttpRequest request = HttpRequest.of(headers, null);

        // then
        assertThat(request.getCookie().get("JSESSIONID")).contains("abc-123");
    }

    @Test
    void requestLine에서_method와_path를_분리한다() {
        HttpRequest request = HttpRequest.of(List.of("GET /index.html HTTP/1.1"), null);

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/index.html");
    }

    @Test
    void URI의_query_string을_파라미터로_분리한다() {
        HttpRequest request = HttpRequest.from(List.of(
                "GET /login?account=gugu&password=password HTTP/1.1"));

        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void query_string이_없으면_parameter는_null이다() {
        HttpRequest request = HttpRequest.from(List.of("GET /login HTTP/1.1"));

        assertThat(request.getParameter("account")).isNull();
    }

    @Test
    void query_string의_인코딩된_문자를_디코딩한다() {
        HttpRequest request = HttpRequest.from(List.of(
                "GET /login?account=gugu%40email.com&password=pass%20word HTTP/1.1"
        ));

        assertThat(request.getParameter("account")).isEqualTo("gugu@email.com");
        assertThat(request.getParameter("password")).isEqualTo("pass word");
    }

    @Test
    void RequestHeader의_body를_분리한다() {
        HttpRequest request = HttpRequest.of(List.of(
                        "POST /login?account=gugu&password=password HTTP/1.1"),
                "account=tion&email=ehfrhfo9494@naver.com&password=password");

        assertThat(request.getBodyParameter("account")).isEqualTo("tion");
    }
}
