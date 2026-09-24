package org.apache.coyote.http11;

import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String requestBody;
    private Session session;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpMethod method() {
        return requestLine.method();
    }

    public String path() {
        return requestLine.path();
    }

    public RequestLine line() {
        return requestLine;
    }

    public HttpCookie cookie() {
        return HttpCookie.from(headers.valueOf("Cookie"));
    }

    public String requestBody() {
        return requestBody;
    }

    public Session getSession() {
        if (session != null) {
            return session;
        }
        final SessionManager sessionManager = SessionManager.getInstance();
        final String jSessionId = cookie().getValue("JSESSIONID");

        return sessionManager.findSession(jSessionId)
            .orElseGet(SessionProvider::provide);
    }
}
