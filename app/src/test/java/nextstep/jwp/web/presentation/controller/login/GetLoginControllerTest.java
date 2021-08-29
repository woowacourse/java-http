package nextstep.jwp.web.presentation.controller.login;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.message.element.Headers;
import nextstep.jwp.http.message.element.cookie.Cookie;
import nextstep.jwp.http.message.element.cookie.HttpCookie;
import nextstep.jwp.http.message.element.session.HttpSession;
import nextstep.jwp.http.message.element.session.HttpSessions;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GetLoginControllerTest {

    @DisplayName("로그인 페이지에 접속한다.")
    @ParameterizedTest
    @MethodSource("parametersForDoService")
    void doService(String uri, int code, String content) {
        final GetLoginController getLoginController = new GetLoginController();
        final HttpRequest httpRequest = Fixture.getHttpRequest(uri);

        final Response response = getLoginController.doService(httpRequest);
        assertThat(response.asString()).contains(String.valueOf(code));
        assertThat(response.asString()).contains(String.valueOf(content));
    }

    private static Stream<Arguments> parametersForDoService() {
        return Stream.of(
                Arguments.of("/login", 200, "로그인"),
                Arguments.of("/login?password=123", 200, "로그인"),
                Arguments.of("/login?account=1", 200, "로그인")
        );
    }

    @DisplayName("이미 로그인 된 상태로 로그인 페이지에 접속한다.")
    @Test
    void doService_alreadyLoggedIn() {
        final GetLoginController getLoginController = new GetLoginController();

        HttpSession httpSession = new HttpSession();
        httpSession.setAttribute("isLogged", true);
        HttpSessions.put(httpSession);

        Cookie cookie = new HttpCookie();
        cookie.put("JSESSIONID", httpSession.getSessionId());

        Headers headers = new Headers();
        headers.putHeader("Cookie", cookie.asString());
        final HttpRequest httpRequest = Fixture.getHttpRequest("/login", headers);

        final Response response = getLoginController.doService(httpRequest);
        assertThat(response.asString()).contains("302");
    }

    @DisplayName("로그인 페이지 컨트롤러 실행 조건을 확인한다.")
    @Test
    void isSatisfiedBy() {
        final GetLoginController getLoginController = new GetLoginController();
        final HttpRequest httpRequest = Fixture.getHttpRequest("/login");

        assertThat(getLoginController.isSatisfiedBy(httpRequest)).isTrue();
    }
}
