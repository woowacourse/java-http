package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LINE_SEPARATOR = "\r\n";
    private static final int MIN_OFF = 0;

    private final RequestLine startLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(final RequestLine startLine, final HttpHeaders headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var startLine = readStartLine(reader);
        final var headers = readHeaders(reader);
        final var body = readBody(reader, headers.getContentLength());

        return new HttpRequest(startLine, headers, body);
    }

    private static RequestLine readStartLine(final BufferedReader reader) throws IOException {
        final var startLine = reader.readLine();

        return RequestLine.from(startLine);
    }

    private static HttpHeaders readHeaders(final BufferedReader bufferedReader) throws IOException {
        StringBuilder headers = new StringBuilder();
        String line;
        while (!(line = Objects.requireNonNull(bufferedReader.readLine())).isBlank()) {
            headers.append(line)
                    .append(LINE_SEPARATOR);
        }

        return HttpHeaders.from(headers.toString());
    }

    private static String readBody(final BufferedReader reader, final String contentLength) throws IOException {
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        reader.read(buffer, MIN_OFF, length);

        return new String(buffer);
    }

    public HttpCookie getCookie() {
        HttpCookie httpCookies = HttpCookie.empty();

        if (headers.hasCookies()) {
            httpCookies = HttpCookie.from(headers.getCookies());
        }
        return httpCookies;
    }

    public Session getSession() throws IOException {
        HttpCookie cookie = getCookie();

        if (cookie.isJSessionId()) {
            String sessionId = cookie.getJSessionId();
            return SessionManager.getInstance().findSession(sessionId);
        }

        Session session = new Session();
        SessionManager.getInstance().add(session);
        return session;
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getBody() {
        return body;
    }

    public boolean isGet() {
        return startLine.isMethodGet();
    }

    public boolean isPost() {
        return startLine.isMethodPost();
    }

    public Optional<User> findUserBySessionId() throws IOException {
        HttpCookie cookie = getCookie();
        if (cookie.isJSessionId()) {
            String sessionId = cookie.getJSessionId();
            Session session = SessionManager.getInstance().findSession(sessionId);
            return Optional.ofNullable((User) session.getAttribute("user"));
        }

        return Optional.empty();
    }
}
