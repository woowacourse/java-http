package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookies;

public class HttpResponseHeader {

    private Map<String, String> httpResponseHeaders;
    private HttpCookies httpCookies;

    public HttpResponseHeader() {
        this.httpResponseHeaders = new HashMap<>();
        this.httpCookies = new HttpCookies();
    }

    public void add(String key, String value) {
        this.httpResponseHeaders.put(key, value);
    }

    public void setJSessionId(String jSessionId) {
        this.httpCookies.addJSessionId(jSessionId);
    }

    public String get(String key) {
        return httpResponseHeaders.get(key);
    }

    public String getSetCookieValue() {
        return this.httpCookies.toSetCookieValue();
    }
}
