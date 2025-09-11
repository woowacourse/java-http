package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.cookie.HttpCookie;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final HttpCookie cookies;
    private final SessionManager sessionManager;

    public HttpRequest(BufferedReader reader, SessionManager sessionManager) throws IOException {
        this.requestLine = parseRequestLine(reader);
        this.headers = Map.copyOf(parseHeaders(reader));
        this.body = parseBody(reader, headers);
        this.cookies = HttpCookie.parse(headers.get("Cookie"));
        this.sessionManager = sessionManager;
    }

    public Map<String, String> parseFormData() {
        if (!hasBody()) {
            return Map.of();
        }

        Map<String, String> params = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return Map.copyOf(params);
    }

    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        final var headers = new HashMap<String, String>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        return headers;
    }

    private RequestLine parseRequestLine(BufferedReader reader) throws IOException {
        final var line = reader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("요청 라인이 없음");
        }

        final var parts = line.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("잘못된 요청 라인: " + line);
        }

        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    private String parseBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return "";
        }

        try {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 Content-Length: " + headers.get("Content-Length"));
        }
    }

    public Session getSession(boolean create) {
        String sessionId = cookies.getJSessionId();

        if (sessionId != null) {
            try {
                Session session = sessionManager.findSession(sessionId);
                if (session != null) {
                    return session;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (create) {
            return sessionManager.createSession();
        }

        return null;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getEndpoint() {
        return requestLine.getEndpoint();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getBody() {
        return body;
    }

    public boolean hasBody() {
        return !body.isEmpty();
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public Session getSession() {
        return getSession(true);
    }
}
