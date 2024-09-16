package org.apache.coyote.http11.message.request;

import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.message.common.HttpCookie;
import org.apache.coyote.http11.message.common.HttpHeader;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.parser.HttpCookieParser;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean isGetMethod() {
        return requestLine.isGet();
    }

    public boolean isPostMethod() {
        return requestLine.isPost();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getCookies() {
        return headers.getCookies();
    }

    public String getBody() {
        return body;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getHost() {
        return getHeaders().getValue(HttpHeader.HOST);
    }

    public Session getSession(boolean enableCreation) {
        HttpCookie httpCookie = HttpCookieParser.parse(headers.getCookies());
        if (httpCookie.hasSessionId()) {
            String sessionId = httpCookie.getSessionId();
            return SessionManager.getInstance().findSession(sessionId);
        }
        if (enableCreation) {
            Session session = new Session(UUID.randomUUID().toString());
            SessionManager.getInstance().add(session);
            return session;
        }
        return null;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "httpLine=" + requestLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
