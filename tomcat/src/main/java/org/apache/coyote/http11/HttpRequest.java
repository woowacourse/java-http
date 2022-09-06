package org.apache.coyote.http11;

import java.util.Map;
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

    public boolean isValidLoginRequest() {
        return isPostRequest() && requestURI.startsWith("/login");
    }

    public boolean isValidRegisterRequest() {
        return isPostRequest() && requestURI.startsWith("/register");
    }

    public boolean isLoginRequestWithAuthorization() {
        return isGetRequest()
            && requestURI.startsWith("/login")
            && cookie.containsAttribute("JSESSIONID");
    }

    private boolean isGetRequest() {
        return method.equals(HttpMethod.GET);
    }

    private boolean isPostRequest() {
        return method.equals(HttpMethod.POST);
    }

    public String getBodyParam(String paramName) {
        return requestBody.getParam(paramName);
    }

    public String getResourcePath() {
        return requestURI.getStaticPath();
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
