package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.resolver.ContentType;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.Cookies;

public class HttpResponseHeader {

    private final Cookies cookies;
    private final Map<String, String> headers;

    public HttpResponseHeader(Cookies cookies, Map<String, String> headers) {
        this.cookies = cookies;
        this.headers = headers;
    }

    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    public void addCookie(Cookie cookie) {
        cookies.addCookie(cookie);
    }

    public boolean hasCookie(String key) {
        return cookies.hasCookie(key);
    }
    
    public static HttpResponseHeader createHeader(ContentType contentType, int contentLength) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType.getContentType());
        headers.put("Content-Length", String.valueOf(contentLength));
        return new HttpResponseHeader(new Cookies(), headers);
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
