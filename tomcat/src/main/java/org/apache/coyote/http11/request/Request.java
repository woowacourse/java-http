package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class Request {
    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestForms requestForms;

    public Request(final RequestLine requestLine, final RequestHeaders requestHeaders,
                   final RequestForms requestForms) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestForms = requestForms;
    }

    public static Request from(final BufferedReader br) throws IOException {
        final RequestLine requestLine = RequestLine.from(br.readLine());
        final RequestHeaders requestHeaders = RequestHeaders.from(br);
        final RequestForms requestForms = createRequestBody(br, requestHeaders);
        return new Request(requestLine, requestHeaders, requestForms);
    }

    private static RequestForms createRequestBody(final BufferedReader br, final RequestHeaders requestHeaders)
            throws IOException {
        if (!requestHeaders.hasContentType()) {
            return new RequestForms(null);
        }
        final int contentLength = Integer.parseInt((String) requestHeaders.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);
        return RequestForms.from(requestBody);
    }

    public boolean noSession() {
        final String sessionId = requestHeaders.getCookieValue("JSESSIONID");
        return SessionManager.findSession(sessionId) == null;
    }

    public Optional<Object> getSessionValue(final String key) {
        final String sessionId = requestHeaders.getCookieValue("JSESSIONID");
        final Session session = SessionManager.findSession(sessionId);
        if (session == null) {
            return Optional.empty();
        }
        return Optional.of(session.getAttribute(key));
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestForms getRequestForms() {
        return requestForms;
    }
}
