package org.apache.coyote.http11.protocol.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    private Session session;

    @BeforeEach
    void setUp() {
        session = new Session("session_id");
    }

    @Test
    @DisplayName("세션 ID 를 반환한다.")
    void getSessionId() {
        String sessionId = session.getId();
        assertThat(sessionId).isEqualTo("session_id");
    }

    @Test
    @DisplayName("세션 속성을 추가하고 조회한다.")
    void setAttribute() {
        session.setAttribute("account", "loki");

        Object attribute = session.getAttribute("account");

        assertThat(attribute).isEqualTo("loki");
    }

    @Test
    @DisplayName("세션 속성을 제거한다.")
    void removeAttribute() {
        session.setAttribute("account", "loki");
        session.removeAttribute("account");

        Object attribute = session.getAttribute("account");

        assertThat(attribute).isNull();
    }

    @Test
    @DisplayName("세션을 무효화하고 모든 속성을 제거한다.")
    void invalidateSession() {
        session.setAttribute("account", "loki");
        session.setAttribute("email", "loki@email.com");

        session.invalidate();

        assertAll(
                () -> assertThat(session.getAttribute("account")).isNull(),
                () -> assertThat(session.getAttribute("email")).isNull()
        );
    }
}
