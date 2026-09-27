package org.apache.coyote.http11;

import java.io.IOException;

public class SessionResolver {

    private final SessionManager sessionManager;

    public SessionResolver(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public Session resolveOrCreate(HttpRequest request, HttpResponse response) throws IOException {
        String sessionId = HttpCookie.parse(request.getHeader("Cookie"))
                .get("JSESSIONID")
                .orElse(null);

        if (sessionId != null) {
            Session existing = sessionManager.findSession(sessionId);
            if (existing != null) {
                return existing;
            }
        }

        Session created = sessionManager.createSession();

        response.setHeader(
                "Set-Cookie",
                "JSESSIONID=" + created.getId() + "; Path=/; HttpOnly"
        );
        return created;
    }
}
