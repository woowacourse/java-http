package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.StatusCode;

public class HttpResponseHeader {
    private StatusCode statusCode;
    private final Map<String, String> payLoads;

    public HttpResponseHeader(StatusCode statusCode) {
        this.statusCode = statusCode;
        this.payLoads = new LinkedHashMap<>();
    }

    public HttpResponseHeader() {
        payLoads = new LinkedHashMap<>();
    }

    public void contentType(String contentType) {
        payLoads.put("Content-Type", contentType);
    }

    public void contentLength(int contentLength) {
        payLoads.put("Content-Length", String.valueOf(contentLength));
    }

    public void setCookie(Cookie cookie) {
        payLoads.put("Set-Cookie", cookie.serialize());
    }

    public void location(String location) {
        payLoads.put("Location", location);
    }

    public void statusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getPayLoads() {
        return payLoads;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}

