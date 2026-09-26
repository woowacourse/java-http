package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> body;
    private final HttpCookie cookies;
    private Session session;

    public HttpRequest(InputStream inputStream) throws IOException {
        String requestLine = readLine(inputStream);
        String[] requestLineParts = requestLine.split(" ", 3);
        this.method = requestLineParts[0];

        String uri = requestLineParts[1];
        this.path = extractPath(uri);
        this.headers = readHeaders(inputStream);
        this.cookies = new HttpCookie(getHeaderOrDefault("Cookie", ""));
        this.body = parseEncodedFormData(readBody(inputStream));
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

    private Map<String, String> readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = readLine(inputStream)).isEmpty()) {
            String[] nameAndValue = line.split(":", 2);
            headers.put(nameAndValue[0].toLowerCase(Locale.ROOT), nameAndValue[1].trim());
        }

        return headers;
    }

    private String getHeaderOrDefault(String name, String defaultValue) {
        return headers.getOrDefault(name.toLowerCase(Locale.ROOT), defaultValue);
    }

    private String readBody(InputStream inputStream) throws IOException {
        int contentLength = Integer.parseInt(getHeaderOrDefault("Content-Length", "0"));
        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IOException("본문을 모두 읽기 전에 연결이 종료되었습니다.");
        }

        return new String(body, StandardCharsets.UTF_8);
    }

    private String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            if (value != '\r') {
                line.write(value);
            }
        }

        return line.toString(StandardCharsets.US_ASCII);
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
