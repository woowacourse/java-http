package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
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

    public HttpRequest(final BufferedReader reader) {
        requestLine = resolveRequestLine(reader);
        headers = new RequestHeaders(reader);
        body = resolveBody(reader);
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

    private RequestLine resolveRequestLine(final BufferedReader reader) {
        try {
            final String line = reader.readLine();

            if (line == null || line.isBlank()) {
                throw new IllegalArgumentException("Request Line이 존재하지 않습니다.");
            }

            return new RequestLine(line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RequestBody resolveBody(final BufferedReader reader) {
        final int contentLength = headers.getContentLength();

        if (contentLength == 0) {
            return new RequestBody("");
        }

        final char[] buffer = new char[contentLength];

        try {
            int offset = 0;

            while (offset < contentLength) {
                final int read = reader.read(
                        buffer,
                        offset,
                        contentLength - offset
                );

                if (read == -1) {
                    throw new IllegalArgumentException("Content-Length보다 Body가 짧습니다.");
                }

                offset += read;
            }

            return new RequestBody(new String(buffer));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
