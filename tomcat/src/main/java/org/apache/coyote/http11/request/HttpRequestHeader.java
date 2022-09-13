package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.authorization.HttpCookie;

public class HttpRequestHeader {

    private final Map<String, Object> headers;

    public HttpRequestHeader(Map<String, Object> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(Map<String, Object> headers) {
        if (headers.containsKey("Cookie")) {
            headers.put("Cookie", HttpCookie.from((String) headers.get("Cookie")));
        }
        return new HttpRequestHeader(headers);
    }

    public Object getHeader(final String key) {
        return headers.get(key);
    }
}
