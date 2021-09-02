package nextstep.jwp.http.session;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestCookie;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpSessions 테스트")
class HttpSessionsTest {

    @DisplayName("로그인 되어있는 사용자 확인 테스트")
    @Test
    void isLoggedIn() {
        //given
        final HttpSession session = HttpSessions.createSession();
        final User user = new User(1L, "inbi", "1234", "inbi@email.com");
        session.setAttribute("user", user);

        //when
        final Map<String, String> cookies = Map.of("JSESSIONID", session.getId());
        final RequestCookie requestCookie = new RequestCookie(cookies);
        final RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>(), requestCookie);
        final HttpRequest request = new HttpRequest(null, requestHeaders, null);

        //then
        assertThat(request.isLoggedIn()).isTrue();
    }

    @DisplayName("로그인 되어있지 않은 사용자 확인 테스트 - 세션 자체가 존재하지 않을 때")
    @Test
    void isNotLoggedInWhenSessionNotExists() {
        //given
        //when
        final Map<String, String> cookies = Map.of("Cookie", "JSESSIONID=" + "1234");
        final RequestCookie requestCookie = new RequestCookie(cookies);
        final RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>(), requestCookie);
        final HttpRequest request = new HttpRequest(null, requestHeaders, null);

        //then
        assertThat(request.isLoggedIn()).isFalse();
    }

    @DisplayName("로그인 되어있지 않은 사용자 확인 테스트 - 세션은 존재하지만, user Attribute가 존재하지 않을 때")
    @Test
    void isNotLoggedInWhenSessionExistsUserAttributeNotExists() {
        //given
        final HttpSession session = HttpSessions.createSession();

        //when
        final Map<String, String> cookies = Map.of("Cookie", "JSESSIONID=" + session.getId());
        final RequestCookie requestCookie = new RequestCookie(cookies);
        final RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>(), requestCookie);
        final HttpRequest request = new HttpRequest(null, requestHeaders, null);

        //then
        assertThat(request.isLoggedIn()).isFalse();
    }

    @DisplayName("로그인 되어있지 않은 사용자 확인 테스트 - 쿠키에 JSESSIONID 키 값이 없을 때")
    @Test
    void isNotLoggedInWhenJSESSIONIDNotExistsInCookie() {
        //given
        //when
        final RequestCookie requestCookie = new RequestCookie(new HashMap<>());
        final RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>(), requestCookie);
        final HttpRequest request = new HttpRequest(null, requestHeaders, null);

        //then
        assertThat(request.isLoggedIn()).isFalse();
    }
}
