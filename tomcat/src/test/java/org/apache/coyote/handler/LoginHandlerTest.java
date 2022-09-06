package org.apache.coyote.handler;

import static ch.qos.logback.classic.Level.INFO;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class LoginHandlerTest {

    @DisplayName("존재하는 회원의 account 로 로그인시 로그인을 성공하여 로그를 남긴다.")
    @Test
    void login() {
        // given
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        final Logger logger = (Logger) LoggerFactory.getLogger(LoginHandler.class);
        logger.addAppender(appender);
        appender.start();

        final String account = "gugu";
        final String password = "password";
        final String requestBody = "account=" + account + "&password=" + password;

        // when
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", Map.of(), requestBody);
        LoginHandler.login(httpRequest);

        // then
        final List<ILoggingEvent> logs = appender.list;
        final String message = logs.get(0).getFormattedMessage();
        final Level level = logs.get(0).getLevel();

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        assertThat(message).isEqualTo(user.toString());
        assertThat(level).isEqualTo(INFO);
    }

    @DisplayName("로그인 실패시에는 401.html 을 반환한다.")
    @Test
    void failToLogin() {
        //given
        final String requestBody = "account=gugu&password=invalid";

        //when
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", Map.of(), requestBody);
        final HttpResponse httpResponse = LoginHandler.login(httpRequest);

        //then
        assertThat(httpResponse.getResponse()).contains("/401.html");
    }

    @DisplayName("정상적으로 요청을 처리한 이후에는 index.html 을 반환한다.")
    @Test
    void returnLoginWhenWithoutQuery() {
        //given
        final String requestBody = "account=gugu&password=password";

        //when
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", Map.of(), requestBody);
        final HttpResponse httpResponse = LoginHandler.login(httpRequest);

        //then
        assertThat(httpResponse.getResponse()).contains("/index.html");
    }

    @DisplayName("로그인 성공 시에 Cookie에 JSESSIONID가 없으면 Cookie 를 담은 응답을 반환한다.")
    @Test
    void setCookie() {
        //given
        final String requestBody = "account=gugu&password=password";

        //when
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", Map.of(), requestBody);
        final HttpResponse httpResponse = LoginHandler.login(httpRequest);

        //then
        assertThat(httpResponse.getResponse()).contains("Set-Cookie: JSESSIONID");
    }

    @DisplayName("요청에 쿠키가 포함되어 있을 경우에는 Set-cookie 를 응답하지 않는다.")
    @Test
    void noSetCookieWhenExist() {
        //given
        final String requestBody = "account=gugu&password=password";

        //when
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", Map.of("Cookie", "JSESSIONID=randomid"),
                requestBody);
        final HttpResponse httpResponse = LoginHandler.login(httpRequest);

        //then
        assertThat(httpResponse.getResponse()).doesNotContain("Set-Cookie: JSESSIONID");
    }

    @DisplayName("로그인 성공 시 세션 저장소(SessionManager)에 유저 정보를 저장한다.")
    @Test
    void saveSession() {
        //given
        final String account = "gugu";
        final String requestBody = "account=" + account + "&password=password";
        final HttpRequest httpRequest = HttpRequest.of("POST /login HTTP/1.1", Map.of(), requestBody);

        //when
        final HttpResponse response = LoginHandler.login(httpRequest);

        //then
        final Cookie cookie = response.getCookie();
        final Session session = SessionManager.findSession(cookie.getValue()).orElseThrow();
        final User user = (User) session.getAttribute("user");
        assertThat(user.getAccount()).isEqualTo(account);
    }

    @DisplayName("로그인에 성공한 이후에는 index.html 로 리다이렉트한다.")
    @Test
    void redirectIndexWhenAfterLogin() {
        //given
        final String account = "gugu";
        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow();

        final Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);

        final String sessionId = session.getId();
        final HttpRequest httpRequest = HttpRequest.of("GET /login HTTP/1.1",
                Map.of("Cookie", "JSESSIONID=" + sessionId), "");

        //when
        final HttpResponse httpResponse = LoginHandler.loginWithGet(httpRequest);

        //then
        assertThat(httpResponse.getResponse()).contains("/index.html");
    }
}
