package org.apache.coyote.http11.model;

import java.util.UUID;

import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;

public class HttpRequest {

    private final HttpMethod method;
    private final HttpRequestURI requestURI;
    private final HttpHeader requestHeaders;
    private final HttpRequestBody requestBody;
    private final HttpCookie cookie;

    public HttpRequest(HttpMethod httpMethod, HttpRequestURI requestURI,
        HttpHeader httpRequestHeaders, HttpRequestBody requestBody, HttpCookie cookie) {
        this.method = httpMethod;
        this.requestURI = requestURI;
        this.requestHeaders = httpRequestHeaders;
        this.requestBody = requestBody;
        this.cookie = cookie;
    }

    public boolean isGetRequest() {
        return method.equals(HttpMethod.GET);
    }

    public boolean isPostRequest() {
        return method.equals(HttpMethod.POST);
    }

    public String getBodyParam(String paramName) {
        return requestBody.getParam(paramName);
    }

    public String getResourcePath() {
        return "static" + requestURI.getPath();
    }

    public String getContentType() {
        String extension = requestURI.getExtension();

        return ContentType.ofExtension(extension)
            .getType();
    }

    public Session getSession() {
        return SessionManager.getSessionManager()
            .findSession(cookie.getAttributeOrDefault("JSESSIONID", ""))
            .orElse(new Session(String.valueOf(UUID.randomUUID())));
    }

    public HttpRequestURI getRequestURI() {
        return requestURI;
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
