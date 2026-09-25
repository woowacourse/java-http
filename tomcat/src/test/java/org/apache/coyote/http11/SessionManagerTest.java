package org.apache.coyote.http11;

import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SessionManagerTest {

    @Test
    void storesAndFindsSession() {
        // given
        final Session session = new Session("session");

        // when
        SessionManager.add(session);

        // then
        Assertions.assertEquals(session, SessionManager.findSession("session"));
    }

    @Test
    void removesSession() {
        // given
        final Session session = new Session("removedSession");
        SessionManager.add(session);

        // when
        SessionManager.remove("removedSession");

        // then
        Assertions.assertNull(SessionManager.findSession("removedSession"));
    }
}
