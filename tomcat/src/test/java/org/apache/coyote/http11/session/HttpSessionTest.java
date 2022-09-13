package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpSessionTest {

    HttpSession httpSession;

    @BeforeEach
    void setUp() {
        httpSession = new HttpSession();
    }

    @DisplayName("세션에 값을 넣는다.")
    @Test
    void put() {
        httpSession.put("myname", "kim");

        assertThat(httpSession.get("myname")).isNotNull();
    }

    @DisplayName("세션에서 값을 조회해 온다.")
    @Test
    void get() {
        httpSession.put("myname1", "kim");
        httpSession.put("myname2", "lee");
        assertThat(httpSession.get("myname2")).isSameAs("lee");
    }
}