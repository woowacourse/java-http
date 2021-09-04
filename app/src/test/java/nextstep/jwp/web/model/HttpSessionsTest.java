package nextstep.jwp.web.model;

import nextstep.jwp.exception.InvalidHttpSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HttpSessionsTest {

    String sessionId;

    @BeforeEach
    void setUp() {
        UUID uuid = UUID.randomUUID();
        sessionId = String.valueOf(uuid.getLeastSignificantBits() + uuid.getLeastSignificantBits());
        HttpSessions.save(new HttpSession(sessionId));
    }

    @DisplayName("세션 ID를 사용해서 인메모리에 저장된 HttpSession 객체를 가져올 수 있다.")
    @Test
    void getSessionWithExistSessionId() {
        // when
        HttpSession session = HttpSessions.getSession(sessionId).orElseThrow(InvalidHttpSessionException::new);

        // then
        assertThat(session.getId()).isEqualTo(sessionId);
    }

    @DisplayName("인메모리에 없는 세션ID로 HttpSession 객체를 가져오면 Optional에 빈 값이 들어있다.")
    @Test
    void getSessionWithNonExistSessionId() {
        // when
        Optional<HttpSession> session = HttpSessions.getSession("존재하지 않는 세션 ID");

        // then
        assertThat(session).isEmpty();
    }

    @DisplayName("세션 ID로 세션을 삭제할 수 있다.")
    @Test
    void removeSession() {
        // when
        HttpSessions.removeSession(sessionId);
        Optional<HttpSession> session = HttpSessions.getSession(sessionId);

        // then
        assertThat(session).isEmpty();
    }

    @DisplayName("세션을 저장한다.")
    @Test
    void save() {
        // given
        HttpSession httpSession = HttpSession.create();

        // when
        HttpSession savedSession = HttpSessions.save(httpSession);

        // then
        assertThat(savedSession).isEqualTo(httpSession);
    }
}