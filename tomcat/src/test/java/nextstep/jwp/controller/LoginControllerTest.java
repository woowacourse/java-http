package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Controller;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.security.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private static final Manager sessionManager = SessionManager.getInstance();

    private final Controller loginController = new LoginController();

    @Test
    @DisplayName("JSESSIONID 쿠키나, QueryString 없이 GET 요청시, login.html을 반환한다.")
    void loginControllerSuccessServiceWhenGetMethodWithoutCookieAndQueryString() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();
        URL url = getClass().getClassLoader().getResource("static/login.html");
        String expected = new String(Files.readAllBytes(Path.of(url.getPath())));
        //when
        loginController.service(RequestFixture.GET_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 200 OK")
                .contains(expected);
    }

    @Test
    @DisplayName("QueryString과 함게 GET 요청시, 올바른 정보인 경우 로그인에 성공하고, Cookie와 함께 /index로 리다이렉트 한다.")
    void loginControllerSuccessServiceWhenGetMethodWithQueryString() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();

        //when
        loginController.service(RequestFixture.LOGIN_WTIH_QUERYSTRIING_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 302 FOUND")
                .contains("Location: /index.html")
                .contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    @DisplayName("JSessionId Cookie와 함게 GET 요청시, 올바른 정보인 경우 로그인에 성공하고, /index로 리다이렉트 한다.")
    void loginControllerSuccessServiceWhenGetMethodWithCookie() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();
        String jSessionId = UUID.randomUUID().toString();
        User user = InMemoryUserRepository.findByAccount("gugu").get();
        Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        //when
        loginController.service(RequestFixture.createLoginRequestWithJSessionId(jSessionId), response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 302 FOUND")
                .contains("Location: /index.html")
                .doesNotContain("Set-Cookie: ");
    }

    @Test
    @DisplayName("Post 요청시, 올바른 정보인 경우 로그인에 성공하고, Cookie와 함께 /index로 리다이렉트 한다.")
    void loginControllerSuccessServiceWhenPostMethod() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();

        //when
        loginController.service(RequestFixture.LOGIN_WITH_BODY_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 302 FOUND")
                .contains("Location: /index.html")
                .contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    @DisplayName("Post 요청시, 올바른 정보가 아니면 쿠키 삭제 요청과 함께 /401.html로 리다이렉트 시킨다.")
    void loginControllerRedirectAndRemovingJSessionIdCookie() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();

        //when
        loginController.service(RequestFixture.createLoginRequestWithJSessionId("asdf"), response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 302 FOUND")
                .contains("Location: /401.html")
                .contains("Set-Cookie: JSESSIONID=; Max-Age=0");
    }

}