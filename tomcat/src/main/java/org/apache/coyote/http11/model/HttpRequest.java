package org.apache.coyote.http11.model;

import java.util.UUID;

import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;

public class HttpRequest {

    private final HttpMethod method;
    private final HttpRequestURI requestURI;
    private final HttpHeaders requestHeaders;
    private final HttpRequestBody requestBody;
    private final HttpCookie cookie;

    public HttpRequest(HttpMethod httpMethod, HttpRequestURI requestURI,
        HttpHeaders httpRequestHeaders, HttpRequestBody requestBody, HttpCookie cookie) {
        this.method = httpMethod;
        this.requestURI = requestURI;
        this.requestHeaders = httpRequestHeaders;
        this.requestBody = requestBody;
        this.cookie = cookie;
    }

    public boolean isLoginRequestWithAuthorization() {
        return isGetRequest()
            && requestURI.pathStartsWith("/login")
            && cookie.containsAttribute("JSESSIONID");
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

        return ContentType.getContentType(extension).getType();
    }

    public Session getSession() {
        return SessionManager.getSessionManager()
            .findSession(cookie.getAttribute("JSESSIONID"))
            .orElse(new Session(String.valueOf(UUID.randomUUID())));
    }

    public String getPath() {
        return requestURI.getPath();
    }
}
