package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionTest {

    @Test
    void 세션에_값을_설정한다() {
        final String name = "hello";
        final String value = "wooteco";
        final Session session = new Session(UUID.randomUUID().toString());

        session.setAttribute(name, value);

        assertThat(session.getAttribute(name)).isEqualTo(value);
    }

    @Test
    void 세션의_값을_삭제한다() {
        final String name = "hello";
        final String value = "wooteco";
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(name, value);

        session.removeAttribute(name);

        assertThat(session.getAttribute(name)).isNull();
    }

    @Test
    void 세션을_무효화한다() {
        final String name = "hello";
        final String value = "wooteco";
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(name, value);

        session.invalidate();

        assertThat(session.getAttribute(name)).isNull();
    }
}
