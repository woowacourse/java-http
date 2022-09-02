package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final HttpURI uri;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> bodyParams;
    private final HttpCookie cookie;

    public HttpRequest(HttpMethod httpMethod, HttpURI uri,
        Map<String, String> httpRequestHeaders, Map<String, String> bodyParms, HttpCookie cookie) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpRequestHeaders = httpRequestHeaders;
        this.bodyParams = bodyParms;
        this.cookie = cookie;
    }

    public boolean isValidLoginRequest() {
        return isPostRequest() && uri.pathStartsWith("/login");
    }

    public boolean isValidRegisterRequest() {
        return isPostRequest() && uri.pathStartsWith("/register");
    }

    public boolean isLoginRequestWithAuthorization() {
        return isGetRequest()
            && uri.pathStartsWith("/login")
            && cookie.containsAttribute("JSESSIONID");
    }

    public boolean isGetRequest() {
        return httpMethod.equals(HttpMethod.GET);
    }

    public boolean isPostRequest() {
        return httpMethod.equals(HttpMethod.POST);
    }

    public String getBodyParam(String paramName) {
        return bodyParams.get(paramName);
    }

    public String getResourcePath() {
        return "static" + uri.getPath();
    }

    public String getContentType() {
        String extension = uri.getExtension();

        if (extension.equals("ico")) {
            return "image/apng";
        }
        return "text/" + extension;
    }

    public Session getSession() {
        return SessionManager.getSessionManager()
            .findSession(cookie.getAttribute("JSESSIONID"))
            .orElse(new Session(String.valueOf(UUID.randomUUID())));
    }

    public String getPath() {
        return uri.getPath();
    }
}
