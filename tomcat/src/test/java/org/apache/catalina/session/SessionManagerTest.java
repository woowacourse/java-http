package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {
    private final SessionManager sessionManager = new SessionManager();

    @Test
    void addAndFind() {
        // given
        final Session session = new Session("manager-add");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("manager-add")).isSameAs(session);
    }

    @Test
    void remove() {
        // given
        final Session session = new Session("manager-remove");
        session.setAttribute("user", "gugu");
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession("manager-remove")).isNull();
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void createSessionRegistersNewSession() {
        // when
        final Session session = sessionManager.createSession();

        // then
        assertThat(session.getId()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    void createSessionIssuesDifferentIds() {
        // when
        final Session first = sessionManager.createSession();
        final Session second = sessionManager.createSession();

        // then
        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    void findUnknownOrNullId() {
        assertThat(sessionManager.findSession("manager-unknown")).isNull();
        assertThat(sessionManager.findSession(null)).isNull();
    }

    @Test
    void sessionsAreNotSharedBetweenManagers() {
        // given
        final Session session = new Session("manager-not-shared");
        sessionManager.add(session);

        // when
        final SessionManager other = new SessionManager();

        // then
        assertThat(other.findSession("manager-not-shared")).isNull();
    }
}
