package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HttpRequest {

    private static final String KEY_VALUE_SEPARATOR = "=";

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody body;

    private HttpResponse response;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static Optional<HttpRequest> from(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null) {
            return Optional.empty();
        }
        final RequestLine requestLine = RequestLine.from(line);
        final HttpHeaders headers = HttpHeaders.from(reader);
        final RequestBody body = RequestBody.of(reader, headers);
        return Optional.of(new HttpRequest(requestLine, headers, body));
    }

    public void bind(final HttpResponse response) {
        this.response = response;
    }

    public Session getSession(final boolean create) {
        final HttpCookie cookie = headers.getCookie();
        if (cookie.hasJSessionId()) {
            final Session session = SessionManager.getInstance().findSession(cookie.getJSessionId());
            if (session != null) {
                return session;
            }
        }
        if (!create) {
            return null;
        }
        return createSession();
    }

    private Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        SessionManager.getInstance().add(session);
        response.setCookie(HttpCookie.JSESSIONID + KEY_VALUE_SEPARATOR + session.getId());
        return session;
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getBodyParams() {
        return body.toParams();
    }
}
