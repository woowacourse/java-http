package org.qupring.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.HttpTomcatRequest;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private static final String SESSION_USER_KEY = "user";

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    void 세션을_추가하고_ID로_조회한다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());

        try {
            // when
            sessionManager.add(session);

            // then
            assertThat(sessionManager.findSession(session.getId()))
                    .isSameAs(session);
        } finally {
            sessionManager.remove(session.getId());
        }
    }

    @Test
    void 세션을_제거한다() {
        // given
        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);

        // when
        sessionManager.remove(session.getId());

        // then
        assertThat(sessionManager.findSession(session.getId())).isNull();
    }

    @Test
    void null_ID는_조회하거나_제거해도_문제가_없다() {
        // when
        Session foundSession = sessionManager.findSession(null);
        sessionManager.remove(null);

        // then
        assertThat(foundSession).isNull();
    }

    @Test
    void 로그인_세션을_생성하고_사용자를_저장한다() {
        // given
        HttpRequest request = request();
        User user = new User("gugu", "password", "gugu@email.com");

        Session session = null;
        try {
            // when
            session = SessionManager.createSession(request, user);

            // then
            assertThat(session.getAttribute(SESSION_USER_KEY)).isSameAs(user);
            assertThat(sessionManager.findSession(session.getId()))
                    .isSameAs(session);
            assertThat(request.getSession(false)).isSameAs(session);
        } finally {
            if (session != null) {
                sessionManager.remove(session.getId());
            }
        }
    }

    private HttpRequest request() {
        return new HttpTomcatRequest(
                HttpMethod.POST,
                "/login",
                "HTTP/1.1",
                null,
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of()
        );
    }
}
