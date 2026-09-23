package org.apache.coyote.http11.response;

import java.util.Map;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.Cookies;

public class HttpResponseHeader {

    private final Cookies cookies;
    private final Map<String, String> headers;

    public HttpResponseHeader(Cookies cookies, Map<String, String> headers) {
        this.cookies = cookies;
        this.headers = headers;
    }

    public void addCookie(Cookie cookie) {
        cookies.addCookie(cookie);
    }

    public boolean hasCookie(String key) {
        return cookies.hasCookie(key);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getResponseHeaderString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }

        String cookieString = cookies.toString();
        if (cookieString != null && !cookieString.isEmpty()) {
            sb.append("Set-Cookie: ").append(cookieString).append(" \r\n");
        }

        sb.append("\r\n");

        return sb.toString();
    }
}
