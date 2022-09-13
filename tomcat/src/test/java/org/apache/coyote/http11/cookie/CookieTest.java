package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    Cookie cookie;

    @BeforeEach
    void setUp() {
        cookie = new Cookie();
    }

    @Test
    @DisplayName("쿠키를 가지고 있는지 확인한다.")
    void hasCookie() {
        cookie.setCookie("JSESSIONID", UUID.randomUUID().toString());

        assertThat(cookie.hasCookie("JSESSIONID")).isTrue();
    }

    @Test
    @DisplayName("쿠키를 가지고 있지 않은지 확인한다.")
    void isEmpty() {
        assertThat(cookie.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("헤더에 들어갈 쿠키 값을 정상적으로 생성하는지 확인한다.")
    void generateCookieEntries() {
        String uuid = UUID.randomUUID().toString();
        cookie.setCookie("JSESSIONID", uuid);

        assertThat(cookie.generateCookieEntries()).isEqualTo("JSESSIONID=" + uuid);
    }
}