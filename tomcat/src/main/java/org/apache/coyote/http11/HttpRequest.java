package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

final class HttpRequest {

    private static final String JSESSIONID = "JSESSIONID";

    private final String method;
    private final URI uri;
    private final QueryParameters queryParameters;
    private final QueryParameters bodyParameters;
    private final HttpHeaders headers;
    private final Manager manager;
    private HttpSession session;

    private HttpRequest(final String method,
                        final URI uri,
                        final QueryParameters queryParameters,
                        final QueryParameters bodyParameters,
                        final HttpHeaders headers,
                        final Manager manager) {
        this.method = method;
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.bodyParameters = bodyParameters;
        this.headers = headers;
        this.manager = manager;
    }

    static HttpRequest from(final BufferedReader reader, final Manager manager) throws IOException {
        List<String> headerLines = readHeaderLines(reader);
        HttpHeaders headers = HttpHeaders.from(headerLines.subList(1, headerLines.size()));
        String body = readBody(reader, headers);
        return of(headerLines.getFirst(), headers, body, manager);
    }

    @Nonnull
    private static String readBody(BufferedReader reader, HttpHeaders headers) throws IOException {
        String body = "";

        final int contentLength = headers.getFirst("Content-Length")
                .map(Integer::parseInt)
                .orElse(0);
        if (contentLength > 0) {
            char[] bodyBuffer = new char[contentLength];
            int read = reader.read(bodyBuffer, 0, contentLength);
            if (read != contentLength) {
                throw new InvalidHttpRequestException("Invalid Content-Length: " + contentLength);
            }
            body = new String(bodyBuffer);
        }
        return body;
    }

    @Nonnull
    private static List<String> readHeaderLines(BufferedReader reader) throws IOException {
        String line;
        List<String> headerLines = new ArrayList<>();
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    static HttpRequest from(final List<String> headerLines) {
        return of(headerLines, null);
    }

    static HttpRequest of(final List<String> headerLines, String body) {
        return of(headerLines, body, new SessionManager());
    }

    static HttpRequest of(final List<String> headerLines, final String body, final Manager manager) {
        final HttpHeaders headers = HttpHeaders.from(headerLines.subList(1, headerLines.size()));
        return of(headerLines.getFirst(), headers, body, manager);
    }

    private static HttpRequest of(final String requestLine,
                                  final HttpHeaders headers,
                                  final String body,
                                  final Manager manager) {
        final String[] requestLineParts = requestLine.split(" ", 3);
        if (requestLineParts.length != 3) {
            throw new InvalidHttpRequestException("Invalid request line: " + requestLine);
        }
        final URI uri = URI.create(requestLineParts[1]);
        final QueryParameters queryParameters = QueryParameters.from(uri.getRawQuery());

        final QueryParameters bodyParameters = QueryParameters.from(body);
        return new HttpRequest(requestLineParts[0], uri, queryParameters, bodyParameters, headers, manager);
    }

    String getMethod() {
        return method;
    }

    String getPath() {
        return uri.getPath();
    }

    boolean matches(final String method, final String path) {
        return this.method.equals(method) && getPath().equals(path);
    }

    String getHeader(final String name) {
        return headers.getFirst(name).orElse(null);
    }

    String getParameter(final String name) {
        return queryParameters.get(name).orElse(null);
    }

    String getBodyParameter(final String name) {
        return bodyParameters.get(name).orElse(null);
    }

    Cookie getCookie() {
        return Cookie.from(headers.getFirst("Cookie").orElse(null));
    }

    HttpSession getSession() {
        return getSession(true);
    }

    HttpSession getSession(final boolean create) {
        if (session != null) {
            return session;
        }

        session = getCookie().get(JSESSIONID)
                .map(this::findSession)
                .orElse(null);

        if (session == null && create) {
            session = new Session(UUID.randomUUID().toString());
            manager.add(session);
        }
        return session;
    }

    private HttpSession findSession(final String id) {
        try {
            return manager.findSession(id);
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }
}
