package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private static final String EMPTY_LINE = "\\r\\n\\r\\n";

    private final String method;
    private final String path;
    private final Map<String, String> body;
    private final HttpCookie cookies;
    private Session session;

    public HttpRequest(String rawHttpRequest) {
        String[] headerAndBody = rawHttpRequest.split(EMPTY_LINE);
        String header = headerAndBody[0];

        String requestLine = header.split("\r\n")[0];
        String[] requestLineParts = requestLine.split(" ", 3);
        this.method = requestLineParts[0];

        String uri = requestLineParts[1];
        this.path = extractPath(uri);
        this.cookies = extractCookies(header);

        String body = headerAndBody.length < 2 ? "" : headerAndBody[1];
        this.body = parseEncodedFormData(body);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public Session findSession() {
        if (session != null) {
            return session;
        }

        if (cookies.hasSessionId()) {
            session = SessionManager.findSession(cookies.getSessionId());
        }
        return session;
    }

    public Session getOrCreateSession() {
        if (findSession() == null) {
            String id = cookies.hasSessionId()
                    ? cookies.getSessionId()
                    : UUID.randomUUID().toString();
            session = new Session(id);
            SessionManager.add(session);
        }

        return session;
    }

    private HttpCookie extractCookies(String header) {
        for (String line : header.split("\r\n")) {
            if (line.startsWith("Cookie:")) {
                return new HttpCookie(line.substring("Cookie:".length()).trim());
            }
        }
        return new HttpCookie("");
    }

    private String extractPath(String uri) {
        if (!uri.contains("?")) {
            return uri;
        }

        int indexOfQueryDelimiter = uri.indexOf("?");
        return uri.substring(0, indexOfQueryDelimiter);
    }

    private Map<String, String> parseEncodedFormData(String rawBody) {
        if (rawBody.isEmpty()) {
            return Map.of();
        }

        Map<String, String> formData = new LinkedHashMap<>();
        for (String rawField : rawBody.split("&")) {
            if (rawField.isEmpty()) {
                continue;
            }

            String[] nameAndValue = rawField.split("=", 2);
            String name = URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8);
            String value = nameAndValue.length < 2
                    ? ""
                    : URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8);
            formData.put(name, value);
        }
        return formData;
    }
}
