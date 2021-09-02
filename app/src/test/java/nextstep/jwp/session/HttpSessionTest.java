package nextstep.jwp.session;

import org.junit.jupiter.api.Test;

import nextstep.jwp.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HttpSessionTest {

    HttpSession httpSession = new HttpSession("656cef62-e3c4-40bc-a8df-94732920ed46");
    User user = new User(1, "account", "password", "email@email.com");

    @Test
    void setAndGet() {
        httpSession.setAttribute("user", user);
        assertEquals(user, httpSession.getAttributes("user"));
    }

    @Test
    void removeAttribute() {
        httpSession.removeAttribute("user");
        assertNull(httpSession.getAttributes("user"));
    }

    @Test
    void invalidate() {
        httpSession.setAttribute("user", user);
        httpSession.setAttribute("user2", user);
        httpSession.setAttribute("user3", user);
        httpSession.invalidate();
        assertNull(httpSession.getAttributes("user"));
    }
}