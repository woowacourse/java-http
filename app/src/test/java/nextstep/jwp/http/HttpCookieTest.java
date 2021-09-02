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
}