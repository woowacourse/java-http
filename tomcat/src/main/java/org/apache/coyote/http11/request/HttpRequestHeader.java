package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.HttpHeader;

public class HttpRequestHeader {

    private Map<String, String> httpRequestHeaders;
    private HttpCookies httpCookies;

    public HttpRequestHeader(Map<String, String> httpRequestHeaders) {
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpCookies = new HttpCookies(httpRequestHeaders.get(HttpHeader.COOKIE));
    }

    public String findBy(String key) {
        return this.httpRequestHeaders.get(key);
    }

    public String getJSessionId() {
        return httpCookies.getJSessionId();
    }
}
