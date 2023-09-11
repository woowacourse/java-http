package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.catalina.Session;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private HttpStatus httpStatus;

    private final Map<String, String> headers = new LinkedHashMap<>();

    private String redirectUri;

    private Session session;

    public HttpResponse httpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponse redirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public HttpResponse header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(final Session session) {
        this.session = session;
    }

}
