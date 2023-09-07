package org.apache.coyote.http11.response;

import lombok.Getter;
import org.apache.coyote.http11.session.HttpCookie;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class HttpResponseHeaders {

    private Map<String, String> headers;

    private HttpResponseHeaders(Map<String, String> headers) {
        this.headers = new LinkedHashMap<>(headers);
    }

    public HttpResponseHeaders() {
        this(new LinkedHashMap<>());
    }

    public String find(String key) {
        return headers.get(key);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public HttpResponseHeaders header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponseHeaders location(String location) {
        if (location == null) {
            return this;
        }
        headers.put("Location", location);
        return this;
    }

    public HttpResponseHeaders setCookie(HttpCookie httpCookie) {
        if (httpCookie == null) {
            return this;
        }
        String jSessionId = httpCookie.findJSessionId();
        if (jSessionId == null) {
            return this;
        }
        setHeader("Set-Cookie", String.format("JSESSIONID=%s", jSessionId));
        return this;
    }

    public HttpResponseHeaders contentType(ContentType contentType) {
        setHeader("Content-Type", contentType.getName() + ";charset=utf-8");
        return this;
    }

    public HttpResponseHeaders contentLength(HttpResponseBody responseBody) {
        String body = responseBody.getBody();
        setHeader("Content-Length", String.valueOf(body.getBytes().length));
        return this;
    }
}
