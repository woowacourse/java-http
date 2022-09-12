package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.catalina.cookie.HttpCookie;
import org.apache.catalina.session.NullSession;
import org.apache.catalina.session.Session;
import org.apache.coyote.support.HttpMethod;

public class HttpRequest {

    private final StartLine startLine;
    private final RequestHeaders headers;
    private final String body;
    private Session session = new NullSession();

    private HttpRequest(StartLine startLine, RequestHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(BufferedReader reader) throws IOException {
        final var startLine = StartLine.of(reader);
        final var headers = RequestHeaders.of(reader);
        final var body = readBody(reader, headers);
        return new HttpRequest(startLine, headers, body);
    }

    private static String readBody(BufferedReader reader, RequestHeaders headers) throws IOException {
        final var contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return "";
        }
        final var buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getUri() {
        return startLine.getUri();
    }

    public boolean isMethodOf(HttpMethod method) {
        return startLine.getMethod().equals(method);
    }

    public Parameters getParameters() {
        if (headers.hasParametersAsBody()) {
            return Parameters.of(body);
        }
        return startLine.getParameters();
    }

    public HttpCookie findCookie(String name) {
        final var cookies = headers.getCookies();
        return cookies.getCookie(name);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
