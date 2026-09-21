package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import com.techcourse.model.User;
import java.io.IOException;
import java.util.Collections;
import org.apache.catalina.Manager;
import org.junit.jupiter.api.Test;

class SessionTest {

    private final Manager manager = new SessionManager();

    @Test
    void keepsLoginUsersSeparatedBySessionId() throws IOException {
        final Session first = createSession("session-a");
        createSession("session-b");

        final User moa = new User("moa", "password", "moa@example.com");
        first.setAttribute("user", moa);

        assertThat(manager.findSession("session-a").getAttribute("user"))
                .isSameAs(moa);
        assertThat(manager.findSession("session-b").getAttribute("user"))
                .isNull();
        assertThat(manager.findSession("unknown")).isNull();
        assertThat(manager.findSession(null)).isNull();
    }

    @Test
    void replacesUserUnderSameAttributeName() {
        final Session session = createSession("session-a");
        final User moa = new User("moa", "password", "moa@example.com");
        final User gugu = new User("gugu", "password", "gugu@example.com");

        session.setAttribute("language", "ko");
        session.setAttribute("user", moa);
        session.setAttribute("user", gugu);

        assertThat(session.getAttribute("user")).isSameAs(gugu);
        assertThat(session.getAttribute("language")).isEqualTo("ko");
        assertThat(Collections.list(session.getAttributeNames()))
                .containsExactlyInAnyOrder("user", "language");
    }

    @Test
    void removesSessionAttributes() {
        final Session session = createSession("session-a");
        final User moa = new User("moa", "password", "moa@example.com");

        session.setAttribute("user", moa);
        session.removeAttribute("user");
        assertThat(session.getAttribute("user")).isNull();

        session.setAttribute("user", moa);
        session.setAttribute("user", null);
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void invalidatesSessionAndRemovesItFromManager() throws IOException {
        final Session first = createSession("session-a");
        final Session second = createSession("session-b");

        first.invalidate();

        assertThat(manager.findSession("session-a")).isNull();
        assertThat(manager.findSession("session-b")).isSameAs(second);
        assertThatIllegalStateException()
                .isThrownBy(() -> first.getAttribute("user"));
        assertThatIllegalStateException()
                .isThrownBy(() -> first.setAttribute("user", "value"));
        assertThatIllegalStateException()
                .isThrownBy(first::invalidate);
    }

    private Session createSession(String id) {
        final Session session = new Session(id, manager);
        manager.add(session);
        return session;
    }
}
