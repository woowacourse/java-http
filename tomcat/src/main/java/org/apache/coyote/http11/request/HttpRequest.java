package org.apache.coyote.http11.request;

import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class HttpRequest {
    private static final String JSESSIONID = "JSESSIONID";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine,
                        final RequestHeaders requestHeaders,
                        final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final RequestHeaders requestHeaders = RequestHeaders.from(bufferedReader);
        final RequestBody requestBody = RequestBody.of(requestHeaders, bufferedReader);
        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Session getSession(final boolean create) {
        if (!create) {
            final Optional<Cookie> cookieOptional = requestHeaders.findCookie(JSESSIONID);
            if (cookieOptional.isPresent()) {
                return SessionManager.findSession(cookieOptional.get().getValue());
            }
        }
        final Session session = Session.create();
        SessionManager.add(session);
        return session;
    }

    public Optional<Cookie> getCookieValue(final String cookieKey) {
        return requestHeaders.findCookie(cookieKey);
    }
}
