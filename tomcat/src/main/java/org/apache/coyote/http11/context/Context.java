package org.apache.coyote.http11.context;

import java.util.UUID;
import org.apache.coyote.http11.response.PostProcessMeta;

public class Context {

    private final HttpCookie cookie;

    public Context(HttpCookie cookie) {
        this.cookie = cookie;
    }

    public Context postProcess(PostProcessMeta meta) {
        HttpCookie requestCookie = meta.getRequest().getContext().getCookie();
        String jsesssionid = requestCookie.getCookies().get("JSESSIONID");
        if (jsesssionid == null) {
            this.cookie.getCookies().put("JSESSIONID", UUID.randomUUID().toString());
        }
        return new Context(this.cookie);
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public String getAsString() {
        return cookie.getAsString();
    }
}
