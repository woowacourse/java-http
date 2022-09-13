package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.cookie.HttpCookie;

public class RequestHeaders {

    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";

    private Map<String, Object> values = new HashMap<>();

    public RequestHeaders(final List<String> unparsedHeaders) {
        parseHeaders(unparsedHeaders);
    }

    private void parseHeaders(final List<String> unparsedHeaders) {
        for (String unparsedHeader : unparsedHeaders) {
            if (COOKIE.equals(unparsedHeader.split(":")[0])) {
                values.put(COOKIE, new HttpCookie(unparsedHeader.split(":")[1].strip()));
                continue;
            }
            values.put(unparsedHeader.split(":")[0], unparsedHeader.split(":")[1].strip());
        }

        if (!values.containsKey(COOKIE)) {
            values.put(COOKIE, HttpCookie.createNewCookie());
        }
    }

    public int getContentLength() {
        if (!values.containsKey(CONTENT_LENGTH)) {
            return 0;
        }
        return Integer.parseInt((String) values.get(CONTENT_LENGTH));
    }

    public Object get(String key) {
        return values.get(key);
    }

    public boolean hasSessionCookie() {
        HttpCookie httpCookie = (HttpCookie) values.get(COOKIE);
        return httpCookie.hasSessionCookie();
    }

    public String getCookie() {
        HttpCookie httpCookie = (HttpCookie) values.get(COOKIE);
        return httpCookie.getAllCookies();
    }

    public String getCookieSessionID() {
        HttpCookie httpCookie = (HttpCookie) values.get(COOKIE);
        return httpCookie.getSessionId();
    }

    public boolean hasContentLength() {
        return getContentLength() > 0;
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "values=" + values +
                '}';
    }
}
