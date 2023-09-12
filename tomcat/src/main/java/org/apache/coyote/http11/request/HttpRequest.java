package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Headers;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;
    private final QueryString queryString;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        requestLine = RequestLine.from(bufferedReader);
        headers = Headers.from(bufferedReader);
        requestBody = initRequestBody(bufferedReader);
        queryString = initQueryString();
    }

    private RequestBody initRequestBody(final BufferedReader bufferedReader) throws IOException {
        if (headers.containsHeader(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
            return RequestBody.of(bufferedReader, contentLength);
        }
        return RequestBody.empty();
    }

    private QueryString initQueryString() {
        if (requestLine.hasQueryString()) {
            return QueryString.of(requestLine.getUri());
        }
        return QueryString.empty();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean isStaticRequest() {
        return requestLine.isStaticRequest();
    }

    public String getExtension() {
        return requestLine.getExtension();
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public boolean isGetMethod() {
        return requestLine.isGetMethod();
    }

    public boolean hasRequestBody() {
        return Objects.nonNull(requestBody);
    }

    public RequestBody initRequestBody() {
        return requestBody;
    }

    public boolean hasCookie() {
        return headers.hasCookie();
    }

    public boolean hasJSessionId() {
        if (hasCookie()) {
            final HttpCookie cookie = headers.getCookie();
            return cookie.hasJSessionId();
        }
        return false;
    }

    public Session getSession(final boolean create) {
        if (hasJSessionId()) {
            final HttpCookie cookie = headers.getCookie();
            final String jsessionid = cookie.getJsessionid();
            Session session = SessionManager.findSession(jsessionid);
            if (Objects.nonNull(session)) {
                return session;
            }
        }

        if (!create) {
            return null;
        }
        final Session session = new Session();
        SessionManager.add(session);
        return session;
    }
}
