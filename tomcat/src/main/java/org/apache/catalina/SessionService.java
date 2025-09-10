package org.apache.catalina;

import com.techcourse.model.User;
import com.techcourse.presentation.HttpRequest;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.MyCookie;

public class SessionService {

    private static final String SESSION_ID_KEY = "JSESSIONID";

    public boolean isValidSession(final HttpRequest request) {
        final HttpCookie httpCookie = new HttpCookie(request);
        if (httpCookie.hasAttribute(SESSION_ID_KEY)) {
            final String token = httpCookie.getAttribute(SESSION_ID_KEY);
            final Session session = SessionManager.getInstance().findSession(token);
            return session != null;
        }
        return false;
    }

    public Optional<String> createSessionCookie(final User user, final HttpRequest request) {
        final HttpCookie httpCookie = new HttpCookie(request);
        if (!httpCookie.hasAttribute(SESSION_ID_KEY)) {
            final UUID token = UUID.randomUUID();
            final MyCookie cookie = new MyCookie(SESSION_ID_KEY, token.toString());

            final Session session = new Session(cookie.getValue());
            session.setAttribute(user.getAccount(), user);
            SessionManager.getInstance().add(session);

            return Optional.of(cookie.getName() + "=" + cookie.getValue());
        }
        return Optional.empty();
    }
}
