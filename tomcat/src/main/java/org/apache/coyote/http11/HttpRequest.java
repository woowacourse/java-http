package org.apache.coyote.http11;

import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private static final String COOKIE = "Cookie";

    private RequestLine requestLine;
    private Headers headers;
    private HttpCookie httpCookie;
    private String body;
    private RequestParameters requestParameters;

    public HttpRequest(final String startLine) {
        String[] splitStartLine = startLine.split(" ");
        this.requestLine = new RequestLine(splitStartLine[0], splitStartLine[1], splitStartLine[2]);
        this.headers = new Headers();
        this.httpCookie = new HttpCookie();
        this.body = "";
        this.requestParameters = RequestParameters.EMPTY_PARAMETERS;
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return requestLine.isSameHttpMethod(httpMethod);
    }

    public String getPath() {
        RequestURI requestURI = requestLine.getRequestURI();
        return requestURI.getPath();
    }

    public void addHeader(final String key, final String value) {
        if (COOKIE.equals(key)) {
            httpCookie.addCookie(value.trim());
        }

        headers.addHeader(key, value.trim());
    }

    public boolean hasHeader(final String key) {
        return headers.hasHeader(key);
    }

    public String getHeader(final String key) {
        return headers.getHeader(key);
    }

    public void addBody(final String body) {
        this.body = body;
        if (body.contains("&") && body.contains("=")) {
            this.requestParameters = new RequestParameters(body);
            return;
        }

        this.requestParameters = RequestParameters.EMPTY_PARAMETERS;
    }

    public boolean hasRequestParameter(final String... keys) {
        return requestParameters.hasParameter(keys);
    }

    public String getRequestParameter(final String key) {
        return requestParameters.getParameter(key);
    }

    public Session getSession() {
        if (httpCookie.hasJSessionId()) {
            String jSessionId = httpCookie.getJSessionId();
            return SessionManager.findSession(jSessionId);
        }

        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        SessionManager.add(session);
        return session;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
}
