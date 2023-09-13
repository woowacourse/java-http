package org.apache.coyote.http11.request;

import common.http.HttpMethod;
import common.http.Request;
import common.http.Session;

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

    @Override
    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    @Override
    public String getPath() {
        return httpRequestLine.getPath();
    }

    @Override
    public String getVersionOfTheProtocol() {
        return httpRequestLine.getVersionOfTheProtocol();
    }

    @Override
    public String getCookie() {
        return httpRequestHeaders.getCookie();
    }

    @Override
    public String getAccount() {
        return httpRequestBody.getAccount();
    }

    @Override
    public String getPassword() {
        return httpRequestBody.getPassword();
    }

    @Override
    public String getEmail() {
        return httpRequestBody.getEmail();
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Session getSession(boolean create) {
        UUID uuid = UUID.randomUUID();
        this.session = new Session(uuid.toString());
        return session;
    }

    @Override
    public boolean hasValidSession() {
        return session != null;
    }

    @Override
    public boolean hasStaticResourcePath() {
        return STATIC_RESOURCE_PATH_PATTERN.matcher(getPath()).find();
    }

    @Override
    public void addSession(Session session) {
        this.session = session;
    }
}
