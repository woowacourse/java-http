package org.apache.coyote.session;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @DisplayName("세션 저장소에 저장되어 있지 않은 세션 ID로 조회시 예외를 던진다.")
    @Test
    void findSession() {
        // given
        final Session session = new Session();
        SessionManager.add(session);

        // when & then
        assertThatThrownBy(() -> SessionManager.findSession("invalidId"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
