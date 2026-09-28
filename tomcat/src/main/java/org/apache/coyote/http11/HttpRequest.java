package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> parameters;
    private final HttpCookie cookies;
    private Session session;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> headers,
                        final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.parameters = parseParameters("POST".equals(requestLine.getMethod()) ? body : requestLine.getQueryString());
        this.cookies = new HttpCookie(headers.get("Cookie"));
    }

    public static HttpRequest read(final InputStream inputStream) throws IOException {
        final var input = new BufferedInputStream(inputStream);
        final var line = readLine(input);
        if (line == null) {
            return new HttpRequest(new RequestLine("GET / HTTP/1.1"), Map.of(), "");
        }
        final var requestLine = new RequestLine(line);
        final var headers = readHeaders(input);
        final var contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        final var body = input.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IOException("Incomplete request body");
        }
        return new HttpRequest(requestLine, headers, new String(body, StandardCharsets.UTF_8));
    }

    private static Map<String, String> readHeaders(final InputStream input) throws IOException {
        final var headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        var line = readLine(input);
        while (line != null && !line.isEmpty()) {
            final var keyValue = line.split(":", 2);
            if (keyValue.length == 2) {
                headers.put(keyValue[0].trim(), keyValue[1].trim());
            }
            line = readLine(input);
        }
        return headers;
    }

    private static String readLine(final InputStream input) throws IOException {
        final var line = new ByteArrayOutputStream();
        var value = input.read();
        if (value == -1) {
            return null;
        }
        while (value != -1 && value != '\n') {
            if (value != '\r') {
                line.write(value);
            }
            value = input.read();
        }
        return line.toString(StandardCharsets.UTF_8);
    }

    private Map<String, String> parseParameters(final String source) {
        final var parameters = new HashMap<String, String>();
        for (final var parameter : source.split("&")) {
            final var keyValue = parameter.split("=", 2);
            if (keyValue.length == 2) {
                parameters.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        return parameters;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public Session renewSession() {
        final var previous = getSession(false);
        if (previous != null) {
            previous.invalidate();
        }
        session = null;
        return getSession(true);
    }

    public Session getSession(final boolean create) {
        if (session == null) {
            session = SessionManager.getInstance().findSession(cookies.getValue(HttpCookie.JSESSIONID));
        }
        if (session == null && create) {
            session = new Session(UUID.randomUUID().toString());
            SessionManager.getInstance().add(session);
        }
        return session;
    }
}
