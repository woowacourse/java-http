package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.HeaderField.COOKIE;

import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class RequestHeaders {

    private final Map<String, String> requestHeaders;

    public RequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public HttpCookie getCookie() {
        if (requestHeaders.containsKey(COOKIE)) {
            String cookie = requestHeaders.get(COOKIE);
            return HttpCookie.from(cookie);
        }
        return HttpCookie.getEmptyValue();
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }
}
