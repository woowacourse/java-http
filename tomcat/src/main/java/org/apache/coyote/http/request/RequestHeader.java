package org.apache.coyote.http.request;

import org.apache.coyote.http.Header;
import org.apache.coyote.http.HttpCookie;

import java.util.List;

public class RequestHeader extends Header {

    private static final String COOKIE = "Cookie";
    private static final String COOKIE_NAME_WITH_SESSION = "JSESSIONID";

    public RequestHeader(List<String> splitHeaders) {
        super(splitHeaders);
    }

    public int getContentLength() {
        return Integer.parseInt(getValue(CONTENT_LENGTH));
    }

    public boolean hasCookieWithSession() {
        HttpCookie cookie = getCookie();
        return cookie.hasCookieName(COOKIE_NAME_WITH_SESSION);
    }

    private HttpCookie getCookie() {
        if (hasHeader(COOKIE)) {
            return HttpCookie.of(getValue(COOKIE));
        }
        return new HttpCookie();
    }

    private String getValue(String header) {
        if (!headers.containsKey(header)) {
            throw new IllegalArgumentException("Header " + header + " not found");
        }
        return headers.get(header);
    }
}
