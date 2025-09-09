package org.apache.coyote.http11;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;

public final class Http11Request {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final String body;
    private final HttpCookie httpCookie;
    private final Manager manager;
    private Session session;

    public Http11Request(
            final String method,
            final String path,
            final Map<String, String> queryParams,
            final Map<String, String> headers,
            final String body,
            final Manager manager
    ) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
        this.httpCookie = new HttpCookie(headers.get("Cookie"));
        this.manager = manager;
    }

    public static Http11Request createInvalid(final Manager manager) {
        return new Http11Request("", "/", Collections.emptyMap(), Collections.emptyMap(), "", manager);
    }

    public Session getSession(final boolean create) {
        if (session != null) {
            return session;
        }
        return httpCookie.getCookie("JSESSIONID")
                .flatMap(manager::findSession)
                .orElseGet(() -> createNewSession(create));
    }

    private Session createNewSession(final boolean create) {
        if (create) {
            final var newSession = new Session(UUID.randomUUID().toString());
            manager.add(newSession);
            this.session = newSession;
            return newSession;
        }
        return null;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getBody() {
        return body;
    }

    public boolean isPost() {
        return "POST".equalsIgnoreCase(method);
    }
}
