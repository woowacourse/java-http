package org.apache.coyote.http11.context;

import java.util.UUID;

public class Context {

    private final Session session;
    private final HttpCookie cookie;

    public Context(Session session, HttpCookie cookie) {
        this.session = session;
        this.cookie = cookie;
    }

    public static Context createNew() {
        return new Context(new Session(UUID.randomUUID().toString()), new HttpCookie());
    }

    public static Context asResponse(Context requestContext) {
        return new Context(requestContext.session, requestContext.cookie.asResponse(requestContext.session.getId()));
    }

    public Session getSession() {
        return session;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public String getAsString() {
        return cookie.getAsString();
    }
}
