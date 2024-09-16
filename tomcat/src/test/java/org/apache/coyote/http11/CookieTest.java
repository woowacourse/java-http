package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @DisplayName("쿠키에 값을 추가한다.")
    @Test
    void add() {
        Cookie cookie = new Cookie(Map.of("name", "kaki"));

        String name = cookie.getValueBy("name");

        assertThat(name).isEqualTo("kaki");
    }

    @DisplayName("key에 대응되는 쿠키가 존재하면 true를 없으면 false를 반환한다.")
    @Test
    void exist() {
        Cookie cookie = new Cookie(Map.of("name", "kaki"));

        boolean exists1 = cookie.exist("name");
        boolean exists2 = cookie.exist("test");

        assertAll(
                () -> assertThat(exists1).isTrue(),
                () -> assertThat(exists2).isFalse()
        );
    }

    @DisplayName("세션이 존재하면 true를 반환한다.")
    @Test
    void containsSession() {
        Cookie cookie = new Cookie(Map.of("JSESSIONID", "abc123"));

        boolean exists = cookie.containsSession();

        assertThat(exists).isTrue();
    }

    @DisplayName("세션이 존재하지 않으면 false를 반환한다.")
    @Test
    void notContainsSession() {
        Cookie cookie = new Cookie(Map.of("name", "kaki"));

        boolean exists = cookie.containsSession();

        assertThat(exists).isFalse();
    }

    @DisplayName("세션 id를 반환한다.")
    @Test
    void getSessionId() {
        Cookie cookie = new Cookie(Map.of("JSESSIONID", "abc123"));

        String sessionId = cookie.getSessionId();

        assertThat(sessionId).isEqualTo("abc123");
    }
}
