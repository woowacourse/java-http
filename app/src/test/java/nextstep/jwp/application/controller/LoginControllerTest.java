package nextstep.jwp.application.controller;

import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.application.model.User;
import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.mapper.ControllerMapper;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import nextstep.jwp.framework.session.HttpSession;
import nextstep.jwp.framework.session.HttpSessions;
import nextstep.jwp.testutils.TestHttpRequestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertContainsBodyString;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertHeaderIncludes;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginControllerTest {

    private final ControllerMapper controllerMapper = ControllerMapper.getInstance();
    private final LoginController loginController = (LoginController) controllerMapper.resolve("/login");

    @DisplayName("GET 요청 - 쿠키에 JSESSIONID 가 없는 경우, 로그인 페이지로 포워딩한다.")
    @Test
    void doGetWhenInvalidSession() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = loginController.doGet(httpRequestMessage);

        // then
        assertLoginPageForwarded(httpResponseMessage);
    }

    @DisplayName("GET 요청 - 쿠키에 JSESSIONID 는 있지만 세션 저장소에는 없는 경우, 로그인 페이지로 포워딩한다.")
    @Test
    void doGetWithInvalidSession() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = loginController.doGet(httpRequestMessage);

        // then
        assertLoginPageForwarded(httpResponseMessage);
    }

    @DisplayName("GET 요청 - 만료된 세션인 경우, 로그인 페이지로 포워딩한다.")
    @Test
    void doGetWithExpiredSession() throws IOException {
        // given
        String sessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpSession expiredSession = new HttpSession(sessionId, -1);
        HttpSessions.add(sessionId, expiredSession);

        String requestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = loginController.doGet(httpRequestMessage);

        // then
        assertLoginPageForwarded(httpResponseMessage);

        // tearDown
        assertThat(HttpSessions.find(sessionId)).isEmpty();
    }

    @DisplayName("GET 요청 - 유효한 세션이지만 세션에 유저가 없는 경우, 로그인 페이지로 포워딩한다.")
    @Test
    void doGetValidSessionButNoUser() throws IOException {
        // given
        String sessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpSession validEmptySession = new HttpSession(sessionId);
        HttpSessions.add(sessionId, validEmptySession);

        String requestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = loginController.doGet(httpRequestMessage);

        // then
        assertLoginPageForwarded(httpResponseMessage);

        // tearDown
        HttpSessions.remove(sessionId);
    }

    @DisplayName("GET 요청 - 유효한 세션이고 세션에 유저가 있는 경우, /index.html 으로 리다이렉트 한다.")
    @Test
    void doGetValidSessionAndExistsUser() throws IOException {
        // given
        String sessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpSession validSession = new HttpSession(sessionId);
        validSession.put("user", new User(1, "ggyool", "password", "ggyool@never.com"));
        HttpSessions.add(sessionId, validSession);

        String requestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = loginController.doGet(httpRequestMessage);

        // then
        assertStatusCode(httpResponseMessage, HttpStatusCode.FOUND);
        assertHeaderIncludes(httpResponseMessage, "Location", "/index.html");

        // tearDown
        HttpSessions.remove(sessionId);
    }

    @DisplayName("POST 요청의 동작을 확인한다.")
    @Test
    void doPost() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = loginController.doPost(httpRequestMessage);

        // then
        assertStatusCode(httpResponseMessage, HttpStatusCode.FOUND);
        assertHeaderIncludes(httpResponseMessage, "Location", "/index.html");
    }

    @DisplayName("비밀번호가 틀린 경우 POST 요청을 확인한다.")
    @Test
    void doPostWithInValidPassword() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=12345");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when, then
        assertThatThrownBy(() -> loginController.doPost(httpRequestMessage))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인에 싪패했습니다");
    }

    @DisplayName("아이디가 없는 경우 POST 요청을 확인한다.")
    @Test
    void doPostWithNonexistentId() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 32",
                "",
                "account=pidgey&password=password");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when, then
        assertThatThrownBy(() -> loginController.doPost(httpRequestMessage))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("해당 아이디가 없습니다");
    }

    @DisplayName("필요한 값이 없는 경우 POST 요청을 확인한다.")
    @Test
    void doPostWithRequiredNull() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when, then
        assertThatThrownBy(() -> loginController.doPost(httpRequestMessage))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("아이디 또는 비밀번호를 입력하지 않았습니다");
    }

    private void assertLoginPageForwarded(HttpResponseMessage httpResponseMessage) {
        assertStatusCode(httpResponseMessage, HttpStatusCode.OK);
        assertContainsBodyString(httpResponseMessage, "로그인");
    }
}
