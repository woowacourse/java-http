package org.apache.session;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionManagerTest {

    @Test
    void 세션_ID_값으로_저장된_세션_식별() {
        User user = new User(1L, "hamad", "password", "email@naver.com");
        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("user", user);
        Session session = new Session(sessionId, userMap);

        SessionManager.add(session);

        assertThat(SessionManager.findSession(sessionId)).isEqualTo(session);
    }

}
