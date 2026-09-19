package org.apache.coyote.http;

import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record HttpServletRequest(
        RequestLine requestLine, HttpHeaders headers, RequestBody body
) {

    private static final Logger log = LoggerFactory.getLogger(HttpServletRequest.class);

    public String path() {
        return requestLine.getUri().getPath();
    }

    public Optional<String> cookie(String key) {
        return headers.getCookie(key);
    }

    public HttpMethod method() {
        return requestLine.getHttpMethod();
    }

    public Session getSession(boolean create) {
        Optional<String> sessionId = headers.getCookie("JSESSIONID");

        if(sessionId.isPresent()) {
            Session found = SessionManager.findSession(sessionId.get());
            if(found != null) {
                log.info("sessionFound: {}",sessionId);
                return found;
            }
        }

        if(!create) {
            return null;
        }

        Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);
        log.info("session created: {}", session.getId());
        return session;
    }
}
