package org.apache.catalina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DisplayName("세션")
class SessionTest {

    @Nested
    @DisplayName("속성 관리")
    class AttributeTests {

        @Test
        @DisplayName("이름으로 속성을 저장하고 조회한다")
        void storesAttributeByName() {
            // given
            final var session = new StandardSession("session-id");

            // when
            session.setAttribute("user", "gugu");

            // then
            assertThat(session.getAttribute("user")).isEqualTo("gugu");
        }

        @Test
        @DisplayName("이름으로 속성을 제거한다")
        void removesAttributeByName() {
            // given
            final var session = new StandardSession("session-id");
            session.setAttribute("user", "gugu");

            // when
            session.removeAttribute("user");

            // then
            assertThat(session.getAttribute("user")).isNull();
        }
    }

    @Nested
    @DisplayName("무효화")
    class InvalidationTests {

        @Test
        @DisplayName("무효화한 세션을 관리자에서 제거한다")
        void invalidationRemovesSessionFromManager() {
            // given
            final var manager = SessionManager.getInstance();
            final var session = manager.createSession();

            // when
            session.invalidate();

            // then
            assertThat(manager.findSession(session.getId())).isNull();
        }

        @Test
        @DisplayName("무효화한 세션의 속성을 조회할 수 없다")
        void invalidatedSessionRejectsAttributeAccess() {
            // given
            final var session = new StandardSession("invalid-session");
            session.invalidate();

            // when
            final var exception = catchThrowable(() -> session.getAttribute("user"));

            // then
            assertThat(exception).isInstanceOf(IllegalStateException.class);
        }
    }
}
