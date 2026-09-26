package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    void 세션_ID가_없으면_새_세션을_만든다() {
        Session session = sessionManager.findOrCreate(null);

        assertThat(session.isNew()).isTrue();
        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    void 저장된_세션_ID면_기존_세션을_반환한다() {
        Session created = sessionManager.findOrCreate(null);

        Session found = sessionManager.findOrCreate(created.getId());

        assertThat(found).isSameAs(created);
        assertThat(found.isNew()).isFalse();
    }

    @Test
    void 서버에_없는_세션_ID면_새_세션을_만든다() {
        Session session = sessionManager.findOrCreate("expired-session-id");

        assertThat(session.isNew()).isTrue();
        assertThat(session.getId()).isNotEqualTo("expired-session-id");
    }

    @Test
    void 세션_ID가_null이면_찾지_못한다() {
        assertThat(sessionManager.findSession(null)).isNull();
    }
}
