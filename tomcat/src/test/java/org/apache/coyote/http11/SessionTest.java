package org.apache.coyote.http11;

import com.techcourse.model.User;
import org.apache.coyote.http11.session.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SessionTest {

    @Test
    void sessionStoreUser() {
        // given
        final Session session = new Session("testSession");
        User user = new User("tory", "1234", "woowa@techcoures");

        // when
        session.setAttribute("user", user);

        // then
        Assertions.assertEquals(user, session.getAttribute("user"));
    }

    @Test
    void sessionRemoveUser() {
        // given
        final Session session = new Session("testSession");
        User user = new User("tory", "1234", "woowa@techcoures");

        // when
        session.setAttribute("user", user);

        // then
        Assertions.assertDoesNotThrow(() -> session.removeAttribute("user"));
    }

    @Test
    void invalidatedSessionCannotBeUsed() {
        // given
        final Session session = new Session("testSession");
        User user = new User("tory", "1234", "woowa@techcoures");

        // when
        session.setAttribute("user", user);
        session.invalidate();

        // then
        Assertions.assertThrows(IllegalStateException.class,
                () -> session.getAttribute("user"));
    }

}
