package org.apache.catalina.session;

import com.techcourse.model.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SessionManagerTest {

    private SessionManager sessionManager = SessionManager.getInstance();
    private User user;
    String sessionId;

    @BeforeEach
    void setUp() {
        user = new User("test", "password", "test@email.com");

        sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    @Test
    @DisplayName("사용자의 JSESSIONID가 저장되어 있다면 해당 JSESSIONID를 반환한다")
    void findSessionId() {
        //when
        String foundSessionId = sessionManager.findSessionId(user);

        //then
        assertThat(foundSessionId).isEqualTo(sessionId);

    }

    @Test
    @DisplayName("사용자의 JSESSIONID가 저장되어 있지 않다면 null 값을 반환한다")
    void findSessionId_fail() {
        //given
        User notLoginedUser = new User("test2", "password2", "test2@email.com");

        //when
        String foundSessionId = sessionManager.findSessionId(notLoginedUser);

        //then
        assertThat(foundSessionId).isNull();
    }
}
