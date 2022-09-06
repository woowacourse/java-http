package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookiesTest {

    @Test
    @DisplayName("쿠키에 값을 저장하고 꺼낸다.")
    void saveAndFind() {
        final Cookies cookies = new Cookies();
        final String key = "name";
        final String value = "alex";
        cookies.addCookie(key, value);

        final String findValue = cookies.getValue(key)
                .get();

        assertThat(findValue).isEqualTo(value);
    }

    @Test
    @DisplayName("세션을 저장하고 찾는다.")
    void saveSessionAndFind() {
        final Cookies cookies = new Cookies();
        final String sessionId = "sessionId";
        cookies.addSession(sessionId);

        final String findSessionId = cookies.getSessionId()
                .get();

        assertAll(
                () -> assertThat(findSessionId).isEqualTo(sessionId),
                () -> assertThat(findSessionId).isEqualTo(cookies.getValue("JSESSIONID").get())
        );
    }
}
