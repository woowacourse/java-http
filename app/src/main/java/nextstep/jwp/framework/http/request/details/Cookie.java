package nextstep.jwp.framework.http.request.details;

import nextstep.jwp.framework.http.common.HttpSession;
import nextstep.jwp.framework.http.common.HttpSessions;
import nextstep.jwp.framework.http.request.util.CookieValueExtractor;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static nextstep.jwp.framework.http.common.Constants.JSESSIONID;

public class Cookie {

    private final Map<String, String> cookieValues;

    private Cookie(final Map<String, String> cookieValues) {
        this.cookieValues = cookieValues;
    }

    public static Cookie of(final String cookieHeader) {
        final Map<String, String> cookieValues = CookieValueExtractor.extract(cookieHeader);
        return new Cookie(cookieValues);
    }

    public String searchValue(final String key) {
        return cookieValues.get(key);
    }

    public HttpSession generateSession() {
        final HttpSession session = getSession();
        if (Objects.isNull(session)) {
            final String sessionId = UUID.randomUUID().toString();
            cookieValues.put(JSESSIONID, sessionId);

            final HttpSession httpSession = new HttpSession(sessionId);
            HttpSessions.addSession(sessionId, httpSession);
            return httpSession;
        }
        return session;
    }

    public HttpSession getSession() {
        if (cookieValues.containsKey(JSESSIONID)) {
            final String sessionId = cookieValues.get(JSESSIONID);
            return HttpSessions.getSession(sessionId);
        }
        return null;
    }
}
