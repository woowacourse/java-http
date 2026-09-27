package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequest {

    private static final String COOKIE = "cookie";
    private static final String JSESSION_ID = "JSESSIONID";

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;
    private Session session;
    private boolean newSession;

    public HttpRequest(final HttpRequestInput input) {
        requestLine = resolveRequestLine(input);
        headers = new RequestHeaders(input);
        body = resolveBody(input);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(final String name) {
        return headers.getHeader(name);
    }

    public RequestBody getBody() {
        return body;
    }

    public Session getSession() {
        if (session != null) {
            return session;
        }

        final Session existingSession = findSession();

        if (existingSession != null) {
            session = existingSession;
            return session;
        }

        session = createSession();
        newSession = true;

        return session;
    }

    public boolean isNewSession() {
        return newSession;
    }

    private Session findSession() {
        final HttpCookie cookie = new HttpCookie(
                headers.getHeaders(COOKIE)
        );

        if (!cookie.contains(JSESSION_ID)) {
            return null;
        }

        final String sessionId = cookie.get(JSESSION_ID);

        return SessionManager.getInstance()
                .findSession(sessionId);
    }

    private Session createSession() {
        final Session newSession = new Session(
                UUID.randomUUID().toString()
        );

        SessionManager.getInstance()
                .add(newSession);

        return newSession;
    }

    private RequestLine resolveRequestLine(final HttpRequestInput input) {
        try {
            final String line = input.readLine();

            if (line == null || line.isBlank()) {
                throw new IllegalArgumentException("Request Line이 존재하지 않습니다.");
            }

            return new RequestLine(line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RequestBody resolveBody(final HttpRequestInput input) {
        final int contentLength = headers.getContentLength();

        if (contentLength == 0) {
            return new RequestBody("");
        }

        try {
            final byte[] bodyBytes = input.readBytes(contentLength);

            return new RequestBody(
                    new String(
                            bodyBytes,
                            StandardCharsets.UTF_8
                    )
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
