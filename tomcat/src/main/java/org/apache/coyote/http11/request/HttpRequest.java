package org.apache.coyote.http11.request;

import common.http.Cookie;
import common.http.Cookies;
import common.http.HttpMethod;
import common.http.Request;
import common.http.Session;
import org.apache.catalina.startup.SessionManager;

import java.util.UUID;
import java.util.regex.Pattern;

public class HttpRequest implements Request {

    private static final Pattern STATIC_RESOURCE_PATH_PATTERN = Pattern.compile("\\.[a-zA-Z0-9]+$");

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final HttpRequestBody httpRequestBody;
    private Session session;

    public HttpRequest(HttpRequestLine httpRequestLine, HttpRequestHeaders httpRequestHeaders, HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public String getVersionOfTheProtocol() {
        return httpRequestLine.getVersionOfTheProtocol();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public boolean hasValidSession() {
        Cookie cookie = Cookie.from(getCookie());
        session = new SessionManager().findSession(Cookies.getJsessionid(cookie));
        return session != null;
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    public String getCookie() {
        return httpRequestHeaders.getCookie();
    }

    public String getAccount() {
        return httpRequestBody.getAccount();
    }

    public String getPassword() {
        return httpRequestBody.getPassword();
    }

    public String getEmail() {
        return httpRequestBody.getEmail();
    }

    public Session getSession(boolean create) {
        UUID uuid = UUID.randomUUID();
        this.session = new Session(uuid.toString());
        new SessionManager().add(session);
        return session;
    }

    public Session getSession() {
        return session;
    }

    public boolean hasStaticResourcePath() {
        return STATIC_RESOURCE_PATH_PATTERN.matcher(getPath()).find();
    }
}
