package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpCookieTest")
class HttpCookieTest {

    @Test
    @DisplayName("세션아이디가 없다면 새로운 세션아이디를 가져온다.")
    void jSessionId() {
        // given
        HttpCookie httpCookie = new HttpCookie(new HashMap<>());
        // when
        String sessionId = httpCookie.jSessionId();
        // then
        assertThat(sessionId).isNotNull()
            .hasToString(httpCookie.jSessionId());
    }

    @Test
    @DisplayName("세션아이디가 있다면 기존 세션 아이디를 가져온다.")
    void jSessionId2() {
        // given
        HashMap<String, String> cookie = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        cookie.put("JSESSIONID", uuid.toString());
        HttpCookie httpCookie = new HttpCookie(cookie);
        // when
        String sessionId = httpCookie.jSessionId();
        // then
        assertThat(sessionId).isNotNull()
            .hasToString(httpCookie.jSessionId());
    }

    @Test
    @DisplayName("쿠키에 들어있는 값을 가져온다.")
    void getCookie() {
        HashMap<String, String> cookie = new HashMap<>();
        cookie.put("yumi", "choco");
        HttpCookie httpCookie = new HttpCookie(cookie);

        assertThat(httpCookie.getCookie("yumi")).isEqualTo("choco");
    }

    @Test
    @DisplayName("쿠키에 들어있는 값이 없다면 Null 을 반환한다.")
    void getCookieWhenNull() {
        HttpCookie httpCookie = new HttpCookie(new HashMap<>());

        assertThat(httpCookie.getCookie("hi")).isNull();
    }
}