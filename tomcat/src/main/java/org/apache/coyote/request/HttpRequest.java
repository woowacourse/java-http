package org.apache.coyote.request;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.PathUrl;
import org.apache.session.Session;
import org.apache.session.SessionManager;

import java.io.IOException;
import java.util.Optional;

public class HttpRequest {
    private final RequestStartLine requestStartLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(
            final RequestStartLine requestStartLine,
            final RequestHeader requestHeader,
            final RequestBody requestBody
    ) {
        this.requestStartLine = requestStartLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final Reader reader) throws IOException {
        final RequestStartLine startLine = RequestStartLine.from(reader.getFirstLine());
        final RequestHeader requestHeader = RequestHeader.from(reader.getHeader());
        final String bodyString = reader.getBody(requestHeader.getContentLength());
        final RequestBody requestBody = RequestBody.from(bodyString);
        return new HttpRequest(startLine, requestHeader, requestBody);
    }

    public boolean hasQueryString() {
        return requestStartLine.hasQueryString();
    }

    public String getQueryValueBy(final String key) {
        return requestStartLine.getQueryValueBy(key);
    }

    public String getPath() {
        return requestStartLine.getPath();
    }

    public boolean isStatic() {
        return requestStartLine.isStatic();
    }

    public boolean isPost() {
        return requestStartLine.isPost();
    }

    public boolean isGet() {
        return requestStartLine.isGet();
    }

    public PathUrl getRequestUrl() {
        return requestStartLine.getRequestUrl();
    }

    public HttpVersion httpVersion() {
        return requestStartLine.getHttpVersion();
    }

    public String getContentType() {
        return requestStartLine.getContentType();
    }

    public String getBodyValue(final String key) {
        return requestBody.getBodyValue(key);
    }


    public Session getSession(final boolean bool) {
        if (bool) {
            final Session session = Session.create();
            SessionManager.add(session);
            return session;
        }
        if (requestHeader.hasJsessionid()) {
            final Optional<Session> session = SessionManager.findSession(requestHeader.getJsessionid());
            if (session.isPresent()) {
                return session.get();
            }
            Session.create(requestHeader.getJsessionid());
        }
        final Session session = Session.create();
        SessionManager.add(session);
        return session;
    }

    @Override
    public String toString() {
        return requestStartLine + "\r\n" +
                requestHeader + "\r\n" + "\r\n" +
                requestBody;
    }
}
