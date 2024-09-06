package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("세션 테스트")
class SessionTest {

    @DisplayName("세션 생성에 성공한다,")
    @Test
    void session() {
        // given
        String key = "user";
        String value = "gugu";
        Session session = Session.createRandomSession();

        // when
        session.setAttribute(key, value);

        // then
        assertThat(session.getId()).isNotNull();
        assertThat(session.getAttribute(key)).isEqualTo(value);
    }

    @DisplayName("초기화에 성공한다.")
    @Test
    void invalidate() {
        // given
        String key = "user";
        String value = "gugu";
        Session session = Session.createRandomSession();
        session.setAttribute(key, value);

        // when
        session.invalidate();

        // then
        assertThat(session.getId()).isNotNull();
        assertThat(session.getAttribute(key)).isNull();
    }
}
