package nextstep.jwp.handler.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionHandlerTest {

    @DisplayName("유효하지 않은 세션 Id로 요청이 들어온 경우 세션을 새로 발급한다")
    @Test
    void getHttpSessionWithInvalidId() {
        RequestLine requestLine = RequestLine.of("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList(
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry;"
        ));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        HttpResponse httpResponse = new HttpResponse();
        HttpSession httpSession = SessionHandler.getHttpSession(httpRequest, httpResponse);

        assertThat(HttpSessions.contains(httpSession)).isTrue();
        assertThat(httpResponse.getHeader("Set-Cookie")).isNotNull();
    }

    @DisplayName("세션 스토리지에 존재하지 않는 세션 id의 경우 새로 발급한다")
    @Test
    void getHttpSessionWithoutStorage() {
        RequestLine requestLine = RequestLine.of("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList(
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        ));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        String oldSessionId = httpRequest.httpCookie().getSessionId();
        assertThat(HttpSessions.contains(oldSessionId)).isFalse();

        HttpResponse httpResponse = new HttpResponse();
        HttpSession httpSession = SessionHandler.getHttpSession(httpRequest, httpResponse);

        assertThat(HttpSessions.contains(httpSession)).isTrue();
        assertThat(httpResponse.getHeader("Set-Cookie")).isNotNull();
    }

    @DisplayName("요청의 SessionId를 읽어 세션을 반환한다")
    @Test
    void getHttpSession() {
        HttpSession existSession = HttpSession.create();
        HttpSessions.register(existSession);

        RequestLine requestLine = RequestLine.of("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList(
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; " + "JSESSIONID=" + existSession.getId()
        ));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        HttpResponse httpResponse = new HttpResponse();
        HttpSession httpSession = SessionHandler.getHttpSession(httpRequest, httpResponse);

        assertThat(HttpSessions.contains(httpSession)).isTrue();
        assertThat(httpResponse.getHeader("Set-Cookie")).isNull();
    }

    @DisplayName("세션 Id와 key에 해당하는 값을 반환한다")
    @Test
    void getSessionValueOrNull() {
        HttpSession existSession = HttpSession.create();
        existSession.setAttribute("user", "user");
        HttpSessions.register(existSession);

        String user = (String)SessionHandler.getSessionValueOrNull(existSession.getId(), "user");
        assertThat(user).isEqualTo("user");
    }

    @DisplayName("유효하지 않은 세션 Id의 경우 null을 반환한다.")
    @Test
    void getSessionWithInvalidSessionId() {
        HttpSession existSession = HttpSession.create();
        existSession.setAttribute("user", "user");
        HttpSessions.register(existSession);

        Object searched = SessionHandler.getSessionValueOrNull(null, "user");
        assertThat(searched).isNull();

        searched = SessionHandler.getSessionValueOrNull(existSession.getId()+1, "user");
        assertThat(searched).isNull();
    }

    @DisplayName("유효하지 않은 key의 경우 null을 반환한다.")
    @Test
    void getSessionWithInvalidKey() {
        HttpSession existSession = HttpSession.create();
        existSession.setAttribute("user", "user");
        HttpSessions.register(existSession);

        Object searched = SessionHandler.getSessionValueOrNull(existSession.getId(), "invalid key");
        assertThat(searched).isNull();
    }
}