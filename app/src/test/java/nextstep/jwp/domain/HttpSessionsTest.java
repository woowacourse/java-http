package nextstep.jwp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionsTest {

    @BeforeEach
    void setUp() {
        HttpSessions.clear();
    }

    @DisplayName("Session을 반환한다.")
    @Test
    void getSession() {
        //given
        String sessionId = "id";
        HttpSession httpSession = new HttpSession(sessionId);
        //when
        HttpSessions.put(sessionId, httpSession);
        //then
        assertThat(HttpSessions.getSession(sessionId).getId()).isEqualTo(sessionId);
        assertThat(HttpSessions.getSession(sessionId)).isEqualTo(httpSession);
    }

    @DisplayName("Session을 추가한다.")
    @Test
    void put() {
        //given
        String sessionId1 = "id1";
        String sessionId2 = "id2";
        HttpSession httpSession1 = new HttpSession(sessionId1);
        HttpSession httpSession2 = new HttpSession(sessionId2);
        //when
        HttpSessions.put(sessionId1, httpSession1);
        HttpSessions.put(sessionId2, httpSession2);
        //then
        assertThat(HttpSessions.size()).isEqualTo(2);
    }
}