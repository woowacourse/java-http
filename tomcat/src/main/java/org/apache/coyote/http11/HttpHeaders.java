package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;
    private HttpCookie cookie = HttpCookie.empty();

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new HashMap<>());
    }

    public static HttpHeaders of(final Map<String, String> headers) {
        return new HttpHeaders(headers);
    }

    public String getHeaderValue(final HttpHeaderType type) {
        return headers.get(type.getName());
    }

    public void setHeaderValue(final HttpHeaderType type, final String value) {
        headers.put(type.getName(), value);
    }

    public void setHeaderValue(final String type, final String value) {
        headers.put(type, value);
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public void setCookie(final HttpCookie httpCookie) {
        this.cookie = httpCookie;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
