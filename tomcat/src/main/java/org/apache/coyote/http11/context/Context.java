package org.apache.coyote.http11.context;

import java.util.UUID;
import org.apache.coyote.http11.response.PostProcessMeta;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class Context implements ResponseHeader {

    private final Session session;
    private final HttpCookie cookie;

    public Context(Session session, HttpCookie cookie) {
        this.session = session;
        this.cookie = cookie;
    }

    public static Context empty() {
        return new Context(null, new HttpCookie());
    }

    public static Context createNew() {
        return new Context(new Session(UUID.randomUUID().toString()), new HttpCookie());
    }

    /*
    change context from requestContext to ResponseContext
     */
    @Override
    public Context postProcess(PostProcessMeta meta) {
        return new Context(session, cookie.asResponse(session.getId()));
    }

    @Override
    public String getAsString() {
        return cookie.getAsString();
    }

    public Session getSession() {
        return session;
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
