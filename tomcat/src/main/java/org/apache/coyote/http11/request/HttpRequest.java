package org.apache.coyote.http11.request;

import org.apache.coyote.http11.*;

import java.io.IOException;
import java.util.Optional;

public class HttpRequest {

    private final StartLine startLine;
    private final Header header;
    private final Body body;

    public HttpRequest(StartLine startLine, Header header, Body body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest create(String plainStartLine, String plainHeader, String plainBody) {
        StartLine startLine = StartLine.create(plainStartLine);
        Header header = Header.create(plainHeader);
        Body body = Body.EMPTY;
        if (!plainBody.trim().isBlank()) {
            body = new Body(plainBody);
        }
        return new HttpRequest(startLine, header, body);
    }

    public String path() {
        return startLine.path();
    }

    public HttpVersion httpVersion() {
        return startLine.httpVersion();
    }

    public HttpMethod httpMethod() {
        return startLine.httpMethod();
    }

    public boolean hasSession() {
        if (!hasCookie()) {
            return false;
        }
        Optional<String> cookieValues = header.findHeaderValueByKey(HttpHeader.COOKIE.value());
        if (cookieValues.isEmpty()) {
            return false;
        }
        Cookie cookie = Cookie.create(cookieValues.get());
        return cookie.findCookieValueByCookieName(Session.JSESSIONID).isPresent();
    }

    private boolean hasCookie() {
        return header.findHeaderValueByKey(HttpHeader.COOKIE.value()).isPresent();
    }

    public Session session() {
        if (hasSession()) {
            try {
                return new SessionManager().findSession(getCookieValue(Session.JSESSIONID));
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage() + System.lineSeparator() + "Invalid Session");
            }
        }
        return null;
    }

    public String getCookieValue(String cookieName) {
        Optional<String> cookieValues = header.findHeaderValueByKey(HttpHeader.COOKIE.value());
        if (cookieValues.isEmpty()) {
            return "";
        }
        Cookie cookie = Cookie.create(cookieValues.get());
        return cookie.findCookieValueByCookieName(cookieName)
                .orElse("");
    }

    public String body() {
        return body.message();
    }

    public Query getQueries() {
        return startLine.getQueries();
    }
}
