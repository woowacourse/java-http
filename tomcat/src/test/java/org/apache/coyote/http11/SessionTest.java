package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    private static final String GOOD = "good";
    private static final String BAD = "bad";
    private static final String DINO = "dino";
    private static final String SESSION_UNIQUE_ID = "sessionUniqueId";

    @DisplayName("세션에 존재하는 value 중 하나를 가져올 수 있다.")
    @Test
    void getAttribute() {
        // given
        final Session session = new Session(SESSION_UNIQUE_ID);
        session.setAttribute(GOOD, DINO);

        // when & then
        assertThat(session.getAttribute(GOOD)).isEqualTo(DINO);
    }


    @DisplayName("해당 키가 없으면 null을 반환한다.")
    @Test
    void getAttribute_notExistKey() {
        // given
        final Session session = new Session(SESSION_UNIQUE_ID);
        session.setAttribute(GOOD, DINO);

        // when & then
        assertThat(session.getAttribute(BAD)).isNull();
    }

    @DisplayName("세션에 attribute를 추가할 수 있다.")
    @Test
    void setAttribute() {
        // given
        final Session session = new Session(SESSION_UNIQUE_ID);

        // when & then
        assertAll(
                () -> assertThat(session.getAttribute(GOOD)).isNull(),
                () -> session.setAttribute(GOOD, DINO),
                () -> assertThat(session.getAttribute(GOOD)).isEqualTo(DINO)
        );
    }

    @DisplayName("세션에 attribute를 삭제할 수 있다.")
    @Test
    void removeAttribute() {
        // given
        final Session session = new Session(SESSION_UNIQUE_ID);
        session.setAttribute(GOOD, DINO);

        // when & then
        assertAll(
                () -> assertThat(session.getAttribute(GOOD)).isEqualTo(DINO),
                () -> session.removeAttribute(GOOD),
                () -> assertThat(session.getAttribute(GOOD)).isNull()
        );
    }

    @DisplayName("세션에 저장된 attribute를 모두 지울 수 있다.")
    @Test
    void invalidate() {
        // given
        final Session session = new Session(SESSION_UNIQUE_ID);
        session.setAttribute(GOOD, DINO);
        session.setAttribute(BAD, DINO);

        // when & then
        assertAll(
                () -> assertThat(session.getAttribute(GOOD)).isEqualTo(DINO),
                () -> assertThat(session.getAttribute(BAD)).isEqualTo(DINO),
                session::invalidate,
                () -> assertThat(session.getAttribute(GOOD)).isNull(),
                () -> assertThat(session.getAttribute(BAD)).isNull()
        );
    }
}
