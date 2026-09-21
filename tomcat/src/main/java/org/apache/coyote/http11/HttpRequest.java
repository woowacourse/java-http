package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import com.techcourse.exception.UncheckedServletException;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

final class HttpRequest {
    private static final String JSESSIONID = "JSESSIONID";

    private final String method;
    private final URI uri;
    private final QueryParameters queryParameters;
    private final QueryParameters bodyParameters;
    private final Cookie cookie;
    private final Manager manager;
    private HttpSession session;

    private HttpRequest(final String method,
                        final URI uri,
                        final QueryParameters queryParameters,
                        final QueryParameters bodyParameters,
                        final Cookie cookie,
                        final Manager manager) {
        this.method = method;
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.bodyParameters = bodyParameters;
        this.cookie = cookie;
        this.manager = manager;
    }

    static HttpRequest from(final BufferedReader reader, final Manager manager) {
        String line;
        List<String> headerLines = new ArrayList<>();
        int contentLength = 0;
        String body = "";

        try {
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                headerLines.add(line);
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            if (contentLength > 0) {
                char[] bodyBuffer = new char[contentLength];
                reader.read(bodyBuffer, 0, contentLength);
                body = new String(bodyBuffer);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return of(headerLines, body, manager);
    }

    static HttpRequest from(final List<String> headerLines) {
        return of(headerLines, null);
    }

    static HttpRequest of(final List<String> headers, String body) {
        return of(headers, body, new SessionManager());
    }

    static HttpRequest of(final List<String> headers, final String body, final Manager manager) {
        final String[] requestLineParts = headers.getFirst().split(" ", 3);
        final URI uri = URI.create(requestLineParts[1]);
        final QueryParameters queryParameters = QueryParameters.from(uri.getRawQuery());

        final QueryParameters bodyParameters = QueryParameters.from(body);
        final Cookie cookie = Cookie.from(findCookieHeader(headers));

        return new HttpRequest(requestLineParts[0], uri, queryParameters, bodyParameters, cookie, manager);
    }

    private static String findCookieHeader(final List<String> headers) {
        for (String header : headers) {
            final int separatorIndex = header.indexOf(':');
            if (separatorIndex <= 0) {
                continue;
            }

            final String name = header.substring(0, separatorIndex).trim();
            if (name.equalsIgnoreCase("Cookie")) {
                return header.substring(separatorIndex + 1).trim();
            }
        }
        return null;
    }

    String getMethod() {
        return method;
    }

    String getPath() {
        return uri.getPath();
    }

    String getParameter(final String name) {
        return queryParameters.get(name).orElse(null);
    }

    String getBodyParameter(final String name) {
        return bodyParameters.get(name).orElse(null);
    }

    Cookie getCookie() {
        return cookie;
    }

    HttpSession getSession() {
        return getSession(true);
    }

    HttpSession getSession(final boolean create) {
        if (session != null) {
            return session;
        }

        session = cookie.get(JSESSIONID)
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
