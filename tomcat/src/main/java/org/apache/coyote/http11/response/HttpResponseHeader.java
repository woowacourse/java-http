package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class HttpResponseHeader {

    private final Map<String, String> headers = new HashMap<>();
    private HttpCookie cookie;

    public HttpResponseHeader() {
        this.cookie = new HttpCookie(new HashMap<>());
    }

    public boolean hasCookie() {
        return cookie.isNotEmpty();
    }

    public void setCookie(final HttpCookie cookie) {
        this.cookie = cookie;
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
