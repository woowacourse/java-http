package nextstep.jwp.handler.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    private final Handler handler = new LoginController();

    @DisplayName("유효하지 않은 세션 Id로 데이터를 저장하는 경우 세션을 새로 발급한다")
    @Test
    void getHttpSessionWithInvalidId() {
        HttpRequest httpRequest = createRequestWithSession("invalidSessionId");
        HttpResponse httpResponse = new HttpResponse();

        handler.handle(httpRequest, httpResponse);
        assertThat(httpResponse.getHeader("Set-Cookie")).isNotNull();
    }

    @DisplayName("세션 스토리지에 존재하지 않는 세션 id로 세션 데이터를 저장하는 경우 세션을 새로 발급한다.")
    @Test
    void getHttpSessionWithoutStorage() {
        HttpSession httpSession = HttpSession.create();
        assertThat(HttpSessions.contains(httpSession.getId())).isFalse();

        HttpRequest httpRequest = createRequestWithSession(httpSession.getId());
        HttpResponse httpResponse = new HttpResponse();

        handler.handle(httpRequest, httpResponse);

        String sessionId = httpResponse.getHeader("Set-Cookie").split("JSESSIONID=")[1];
        assertThat(sessionId).isNotNull();
        assertThat(HttpSessions.contains(sessionId)).isTrue();
    }

    @DisplayName("이미 유효한 세션이 있는 경우 신규 발급하지 않는다.")
    @Test
    void getHttpSession() {
        HttpSession validSession = HttpSession.create();
        HttpSessions.addSession(validSession);

        HttpRequest httpRequest = createRequestWithSession(validSession.getId());
        HttpResponse httpResponse = new HttpResponse();

        handler.handle(httpRequest, httpResponse);

        assertThat(HttpSessions.contains(httpRequest.getSession().getId())).isTrue();
        assertThat(httpResponse.getHeader("Set-Cookie")).isNull();
    }

    private HttpRequest createRequestWithSession(String sessionId) {
        String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList(
                "Content-Length: " + requestBody.length(),
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID="+sessionId
        ));
        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }
}