package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final Map<String, String> queryParams;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> bodyParams;
    private final HttpCookie cookie;
    private final String path;

    public HttpRequest(HttpMethod httpMethod, String path, Map<String, String> queryParams,
        Map<String, String> httpRequestHeaders, Map<String, String> bodyParms, HttpCookie cookie) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.httpRequestHeaders = httpRequestHeaders;
        this.bodyParams = bodyParms;
        this.cookie = cookie;
    }

    public boolean isValidLoginRequest() {
        return isPostRequest() && path.startsWith("/login");
    }

    public boolean isValidRegisterRequest() {
        return isPostRequest() && path.startsWith("/register");
    }

    public boolean isLoginRequestWithAuthorization() {
        return isGetRequest()
            && path.startsWith("/login")
            && cookie.containsAttribute("JSESSIONID");
    }

    private boolean isGetRequest() {
        return httpMethod.equals(HttpMethod.GET);
    }

    private boolean isPostRequest() {
        return httpMethod.equals(HttpMethod.POST);
    }

    public String getBodyParam(String paramName) {
        return bodyParams.get(paramName);
    }

    public String getResourcePath() {
        return "static" + path;
    }

    public String getContentType() {
        String extension = StringUtils.substringAfterLast(path, ".");

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
        return path;
    }
}
