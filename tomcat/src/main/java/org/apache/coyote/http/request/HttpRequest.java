package org.apache.coyote.http.request;

import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record HttpRequest(
        RequestLine requestLine, HttpHeaders headers, RequestBody body
) {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private static final String COOKIE = "Cookie";
    private static final String SESSION_ID = "JSESSIONID";

    public String path() {
        return requestLine.getUri().getPath();
    }

    public Optional<String> cookie(String key) {
        return headers.get(COOKIE)
                .map(HttpCookie::from)
                .flatMap(cookies -> cookies.get(key));
    }

    public HttpMethod method() {
        return requestLine.getHttpMethod();
    }

    public Session getSession(boolean create) {
        Optional<String> sessionId = cookie(SESSION_ID);

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
