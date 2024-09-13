package org.was.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 세션은_생성될때_자동으로_ID_생성() {
        // given, when
        Session session = new Session();

        // then
        assertThat(session.getId()).isNotNull();
    }

    @Test
    void 세션의_아이디는_랜덤한_UUID로_생성() {
        // given, when
        Session firstSession = new Session();
        Session secondSession = new Session();

        // then
        assertAll(
                () -> assertThat(firstSession.getId()).matches("^[a-f0-9\\-]{36}$"),
                () -> assertThat(secondSession.getId()).matches("^[a-f0-9\\-]{36}$"),
                () -> assertThat(firstSession.getId()).isNotEqualTo(secondSession.getId())
        );
    }

    @Test
    void 세션_값을_설증() {
        // given
        String attribute = "user";
        String value = "ted";

        Session session = new Session();

        // when
        session.setAttribute(attribute, value);

        // then
        assertThat(session.hasAttribute(attribute)).isTrue();
    }
}
