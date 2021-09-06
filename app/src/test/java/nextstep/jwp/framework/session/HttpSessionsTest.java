package nextstep.jwp.framework.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionsTest {

    @DisplayName("HttpSessions 에 세션을 넣고 가져온다.")
    @Test
    void addAndFind() {
        // given
        String sessionId = "sessionId";
        HttpSession httpSession = new HttpSession(sessionId);

        assertThat(httpSession.take(sessionId)).isEmpty();

        // when, then
        HttpSessions.add(sessionId, httpSession);
        assertThat(HttpSessions.find(sessionId)).get().isSameAs(httpSession);

        // tearDown
        HttpSessions.remove(sessionId);
    }

    @DisplayName("HttpSessions 에서 존재하지 않는 세션을 가져온다.")
    @Test
    void findNonexistentSession() {
        assertThat(HttpSessions.find("ggyool"))
                .isInstanceOf(Optional.class)
                .isEmpty();
    }

    @DisplayName("Session ID 로 세션을 지운다.")
    @Test
    void remove() {
        // given
        String sessionId = "sessionId";
        HttpSession httpSession = new HttpSession(sessionId);
        HttpSessions.add(sessionId, httpSession);

        assertThat(HttpSessions.find(sessionId)).isNotEmpty();

        // when
        HttpSessions.remove(sessionId);

        // then
        assertThat(HttpSessions.find(sessionId)).isEmpty();
    }
}
