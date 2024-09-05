package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class RequestHeader {

    private final String CONTENT_LENGTH = "Content-Length";
    private final String COOKIE = "Cookie";

    private final Map<String, String> header;

    public RequestHeader() {
        this.header = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public boolean hasContentLength() {
        return header.containsKey(CONTENT_LENGTH);
    }

    public int getContentLength() {
        return Integer.parseInt(header.get(CONTENT_LENGTH));
    }

    public boolean existsSession() {
        if (!header.containsKey(COOKIE)) {
            return false;
        }

        HttpCookie cookies = getCookies();
        return cookies.containsJSessionId();
    }

    public HttpCookie getCookies() {
        return new HttpCookie(header.get(COOKIE));
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "header=" + header +
                '}';
    }
}
