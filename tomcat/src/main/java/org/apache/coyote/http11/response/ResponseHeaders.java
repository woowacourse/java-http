package org.apache.coyote.http11.response;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ResponseHeaders {
    private final Map<String, String> headers = new LinkedHashMap<>();

    public ResponseHeaders init() {
        return new ResponseHeaders();
    }

    public ResponseHeaders addContentType(final String value) {
        headers.put("Content-Type", value);
        return this;
    }

    public ResponseHeaders addContentLength(final String body) {
        if (!body.isBlank()) {
            headers.put("Content-Length", body.getBytes().length + " ");
        }
        return this;
    }

    public ResponseHeaders addLocation(final String location) {
        headers.put("Location", location);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ResponseHeaders addCookie(final HttpRequest httpRequest) {
        final HttpCookie cookie = HttpCookie.parseCookie(httpRequest.getRequestHeaders().getValue("Cookie"));
        if (!cookie.checkIdInCookie()) {
            final UUID sessionId = UUID.randomUUID();
            createSession(sessionId.toString());
            headers.put("Set-Cookie", cookie.makeCookieValue(sessionId));
        }
        return this;
    }

    private static void createSession(final String sessionId) {
        final Session session = new Session(sessionId);
        final SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);
    }
}
