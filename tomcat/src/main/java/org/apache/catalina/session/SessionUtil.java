package org.apache.catalina.session;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SessionUtil {

    private static final Logger log = LoggerFactory.getLogger(SessionUtil.class);
    private static final String USER_ATTRIBUTE = "user";
    private static final String JSESSIONID = "JSESSIONID";

    public static User getCurrentUser(HttpRequest request) {
        HttpCookie cookie = HttpCookie.from(request.headers().get("Cookie"));
        String sessionId = cookie.getValue(JSESSIONID);
        if (sessionId != null) {
            Session session = SessionManager.getSession(sessionId);
            if (session != null) {
                return (User) session.getAttribute(USER_ATTRIBUTE);
            }
        }
        return null;
    }

    public static String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute(USER_ATTRIBUTE, user);
        SessionManager.add(session);
        log.info("세션 생성: sessionId={}, user={}", sessionId, user.getAccount());
        return sessionId;
    }
}