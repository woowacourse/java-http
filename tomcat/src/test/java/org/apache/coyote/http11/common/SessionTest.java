package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Session 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionTest {

    @Test
    void 세선값_저장() {
        // given
        final String id = "randomUUID";
        final Session session = new Session(id);
        final String key = "randomUUIDUser";
        final User value = new User("royce", "password", "email");

        // when
        session.setAttribute(key, value);

        // then
        assertThat(session.getAttribute(key)).isEqualTo(value);
    }

    @Test
    void 세션에_저장된_값_조회() {
        // given
        final String id = "randomUUID";
        final Session session = new Session(id);
        final String key = "randomUUIDUser";
        final User value = new User("royce", "password", "email");
        session.setAttribute(key, value);

        // when
        final Object attribute = session.getAttribute(key);

        // then
        assertThat(attribute).isInstanceOf(User.class);
        assertThat(attribute).isEqualTo(value);
    }

    @Test
    void 세선에_저장된_값_삭제() {
        // given
        final String id = "randomUUID";
        final Session session = new Session(id);
        final String key = "randomUUIDUser";
        final User value = new User("royce", "password", "email");
        session.setAttribute(key, value);

        // when
        session.removeAttribute(key);

        // then
        assertThat(session.getAttribute(key)).isNull();
    }

    @Test
    void 세선_초기화() {
        // given
        final String id = "randomUUID";
        final Session session = new Session(id);
        final String userKey = "randomUUIDUser";
        final String dateKey = "randomUUIDUser";
        final User value = new User("royce", "password", "email");
        session.setAttribute(userKey, value);
        session.setAttribute(dateKey, LocalDateTime.now());

        // when
        session.invalidate();

        // then
        assertThat(session.getAttribute(userKey)).isNull();
        assertThat(session.getAttribute(dateKey)).isNull();
    }
}
