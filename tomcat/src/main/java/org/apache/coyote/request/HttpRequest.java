package org.apache.coyote.request;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SimpleHttpSession;
import org.apache.catalina.session.SimpleManager;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String body;
    @Nullable
    private HttpSession session;

    private HttpRequest(
            final RequestLine requestLine,
            final Map<String, String> headers,
            final Map<String, String> parameters,
            final String body
    ) {
        Objects.requireNonNull(requestLine, "requestLine must not be null");
        Objects.requireNonNull(headers, "headers must not be null");
        Objects.requireNonNull(parameters, "parameters must not be null");
        Objects.requireNonNull(body, "body must not be null");
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.parameters = Map.copyOf(parameters);
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var line = reader.readLine();
        final var requestLine = RequestLine.from(line);

        final var headers = new HashMap<String, String>();
        while (true) {
            final var headerLine = reader.readLine();
            if (headerLine.isBlank()) {
                break;
            }

            final var parts = headerLine.split(":", 2);
            if (parts.length == 2) {
                headers.put(parts[0].trim(), parts[1].trim());
            }
        }

        final var parameters = new HashMap<String, String>();
        parseParameters(requestLine.getQueryString(), parameters);

        var body = "";
        if ("POST".equalsIgnoreCase(requestLine.getMethod())) {
            final var contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            if (contentLength > 0) {
                var bodyChars = new char[contentLength];
                reader.read(bodyChars);
                body = new String(bodyChars);
                parseParameters(body, parameters);
            }
        }

        return new HttpRequest(requestLine, headers, parameters, body);
    }

    private static void parseParameters(final String queryString, final Map<String, String> parameters) {
        if (queryString == null || queryString.isBlank()) {
            return;
        }

        final var pairs = queryString.split("&");
        for (final var pair : pairs) {
            final var keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                parameters.put(
                        URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                );
            }
        }
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public HttpSession getSession(final boolean create) {
        if (session != null) {
            return session;
        }

        final var cookieHeader = getHeader("Cookie");
        final var cookie = RequestCookie.from(cookieHeader);

        if (cookie.contains("JSESSIONID")) {
            final var sessionId = cookie.get("JSESSIONID");
            final var existing = SimpleManager.getInstance()
                    .findSession(sessionId);
            if (existing != null) {
                this.session = existing;
                return session;
            }
        }

        if (!create) {
            return null;
        }

        final var newSession = SimpleHttpSession.ofGeneratedId();
        SimpleManager.getInstance()
                .add(newSession);
        this.session = newSession;
        
        return session;
    }
}
