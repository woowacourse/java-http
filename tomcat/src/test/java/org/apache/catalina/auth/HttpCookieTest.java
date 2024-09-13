package org.apache.catalina.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("성공 : 쿠키 추가")
    void addCookie() {
        HttpCookie httpCookie = new HttpCookie();
        String key = "key";
        String value = "value";

        httpCookie.addCookie(key, value);

        assertThat(httpCookie.getCookie(key)).isEqualTo(value);
    }

    @Test
    @DisplayName("성공 : 세션 정보 추가")
    void addAuthSessionId() {
        HttpCookie httpCookie = new HttpCookie();
        String id = "dd0sef-ewr2mds0-dd";

        httpCookie.addAuthSessionId(id);

        assertThat(httpCookie.getAuthSessionId()).isEqualTo(id);
    }
}
