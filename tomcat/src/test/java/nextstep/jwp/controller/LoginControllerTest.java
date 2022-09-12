package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Status;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LoginControllerTest {

    Map<String, String> requestHeaders = new HashMap<>();

    @BeforeEach
    void setUp() {
        requestHeaders.put("Host", "localhost:8080");
        requestHeaders.put("Connection", "keep-alive");
    }

    @DisplayName("세션에 유저가 존재하는 경우 PAGE_INDEX로 리다이렉트 됨을 확인한다.")
    @Test
    void redirectIndexHtmlWhenUserExistedInSession() throws Exception {
        Session session = new Session();
        session.setAttribute("user", new User(1L, "gugu", "password", "hkkang@woowahan.com"));
        SessionManager.getInstance().add(session);

        RequestLine startLine = RequestLine.from("GET /login HTTP/1.1 ");
        requestHeaders.put("Cookie", "JSESSIONID=" + session.getId());
        HttpHeaders headers = new HttpHeaders(requestHeaders);

        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

        LoginController loginController = new LoginController();

        HttpResponse httpResponse = loginController.doGet(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.FOUND),
                () -> assertThat(httpResponse.getHeaders().getLocation()).isEqualTo("/index.html")
        );
    }

    @DisplayName("세션에 유저가 존재하지 않는 경우 login.html 을 보여준다.")
    @Test
    void showLoginHtmlWhenUserNotExistedInSession() throws Exception {
        RequestLine startLine = RequestLine.from("GET /login HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);

        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

        LoginController loginController = new LoginController();

        HttpResponse httpResponse = loginController.doGet(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.OK),
                () -> assertThat(httpResponse.getHeaders().getContentType()).isEqualTo("text/html;charset=utf-8")
        );
    }

    @DisplayName("userService login에 실패할 경우 401 페이지로 리다이렉트됨을 확인한다.")
    @ParameterizedTest
    @ValueSource(strings = {"account=invalid_gugu&password=password",
            "account=gugu&password=invalid_password"})
    void redirect401WhenLoginFailed(String body) throws Exception {
        RequestLine startLine = RequestLine.from("POST /login HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);

        HttpRequest httpRequest = new HttpRequest(startLine, headers, body);

        LoginController loginController = new LoginController();

        HttpResponse httpResponse = loginController.doPost(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.FOUND),
                () -> assertThat(httpResponse.getHeaders().getLocation()).isEqualTo("/401.html")
        );
    }

    @DisplayName("로그인에 성공한 경우 index.html 페이지로 리다이렉트됨을 확인한다.")
    @Test
    void redirectIndexHtmlWhenLoginSuccess() throws Exception {
        RequestLine startLine = RequestLine.from("POST /login HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);
        String body = "account=gugu&password=password";

        HttpRequest httpRequest = new HttpRequest(startLine, headers, body);

        LoginController loginController = new LoginController();

        HttpResponse httpResponse = loginController.doPost(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.FOUND),
                () -> assertThat(httpResponse.getHeaders().getLocation()).isEqualTo("/index.html")
        );
    }
}
