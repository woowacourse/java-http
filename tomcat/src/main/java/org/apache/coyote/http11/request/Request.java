package org.apache.coyote.http11.request;

import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class Request {
    private static final String JSESSIONID = "JSESSIONID";

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    private Request(final RequestLine requestLine,
                    final RequestHeader requestHeader,
                    final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final RequestHeader requestHeader = RequestHeader.from(bufferedReader);
        final RequestBody requestBody = RequestBody.of(requestHeader, bufferedReader);
        return new Request(requestLine, requestHeader, requestBody);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public Session getSession(final boolean create) {
        if (!create) {
            final Optional<Cookie> cookieOptional = requestHeader.getCookieValue(JSESSIONID);
            if (cookieOptional.isPresent()) {
                return SessionManager.findSession(cookieOptional.get().getValue());
            }
        }
        final Session session = Session.create();
        SessionManager.add(session);
        return session;
    }

    public boolean hasSession() {
        return requestHeader.getCookieValue(JSESSIONID).isPresent();
    }

    public String getCookieValue(final String cookieKey) {
        final Cookie cookie = requestHeader.getCookieValue(cookieKey).orElseThrow();
        return cookie.getValue();
    }
}
