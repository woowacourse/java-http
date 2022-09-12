package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import nextstep.jwp.model.User;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.request.HttpHeader;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestBody;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("존재하는 회원의 account 로 로그인시 로그인을 성공하여 로그를 남긴다.")
    @Test
    void login() {
        // given
        final String account = "gugu";
        final String password = "password";
        final String requestBody = "account=" + account + "&password=" + password;

        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", HttpHeader.from(Map.of()),
                HttpRequestBody.from(requestBody));
        final HttpResponse httpResponse = HttpResponse.from(new ByteArrayOutputStream());
        final LoginController loginController = new LoginController();

        // when & then
        assertDoesNotThrow(() ->loginController.service(httpRequest, httpResponse));
    }

    @DisplayName("로그인 성공 시에 Cookie에 JSESSIONID가 없으면 Cookie 를 담은 응답을 반환한다.")
    @Test
    void setCookie() throws Exception {
        // given
        final String requestBody = "account=gugu&password=password";

        // when
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", HttpHeader.from(Map.of()),
                HttpRequestBody.from(requestBody));
        final HttpResponse httpResponse = HttpResponse.from(new ByteArrayOutputStream());
        final LoginController loginController = new LoginController();
        loginController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getCookie().isJSessionCookie()).isTrue();
    }

    @DisplayName("요청에 쿠키가 포함되어 있을 경우에는 Set-cookie 를 응답하지 않는다.")
    @Test
    void noSetCookieWhenExist() throws Exception {
        // given
        final String requestBody = "account=gugu&password=password";

        // when
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1",
                HttpHeader.from(Map.of("Cookie", "JSESSIONID=randomid")),
                HttpRequestBody.from(requestBody));
        final HttpResponse httpResponse = HttpResponse.from(new ByteArrayOutputStream());
        final LoginController loginController = new LoginController();
        loginController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getCookie().isEmpty()).isTrue();
    }

    @DisplayName("로그인 성공 시 세션 저장소(SessionManager)에 유저 정보를 저장한다.")
    @Test
    void saveSession() throws Exception {
        // given
        final String account = "gugu";
        final String requestBody = "account=" + account + "&password=password";
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", HttpHeader.from(Map.of()), HttpRequestBody.from(requestBody));

        // when
        final HttpResponse httpResponse = HttpResponse.from(new ByteArrayOutputStream());
        final LoginController loginController = new LoginController();
        loginController.service(httpRequest, httpResponse);

        // then
        final Cookie cookie = httpResponse.getCookie();
        final Session session = SessionManager.findSession(cookie.getValue());
        final User user = (User) session.getAttribute("user");
        assertThat(user.getAccount()).isEqualTo(account);
    }
}
