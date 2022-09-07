package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class RequestHeaders {

    private final Map<String, String> requestHeaders;

    public RequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public HttpCookie getCookie() {
        if (requestHeaders.containsKey("Cookie")) {
            String cookie = requestHeaders.get("Cookie");
            return HttpCookie.from(cookie);
        }
        return HttpCookie.getEmptyValue();
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }
}
