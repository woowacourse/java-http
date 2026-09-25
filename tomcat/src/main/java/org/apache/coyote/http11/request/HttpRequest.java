package org.apache.coyote.http11.request;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpRequest {

    private static final Manager SESSION_MANAGER = SessionManager.getInstance();

    private final HttpRequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpParameters body;
    private final HttpCookie cookie;

    private HttpRequest(HttpRequestLine requestLine, Map<String, String> headers,
                        HttpParameters body, HttpCookie cookie) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.body = body;
        this.cookie = cookie;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        String startLine = readLine(bufferedInputStream);
        HttpRequestLine requestLine = HttpRequestLine.from(startLine);

        Map<String, String> headers = readHeaders(bufferedInputStream);

        HttpCookie cookie = HttpCookie.from(headers.get("Cookie"));

        HttpParameters body = readBody(headers, bufferedInputStream);

        return new HttpRequest(requestLine, headers, body, cookie);
    }

    public boolean isMatched(String method, String path) {
        return requestLine.isMatched(method, path);
    }

    public boolean hasBodyParameters(String... names) {
        return Arrays.stream(names).allMatch(body::containsKey);
    }

    public String getBodyParameter(String key) {
        return body.getParameter(key);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpSession getSession(boolean isCreate) throws IOException {
        HttpSession session = SESSION_MANAGER.findSession(cookie.getJsessionid());
        if (isCreate && session == null) {
            String id = UUID.randomUUID().toString();
            Session newSession = new Session(id);
            SESSION_MANAGER.add(newSession);
            return newSession;
        }
        return session;
    }

    public String getJsessionid() {
        return cookie.getJsessionid();
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();

        int current;
        while ((current = inputStream.read()) != '\r' && current != -1) {
            line.write(current);
        }

        if (current == -1) {
            return convertLineOrNull(line);
        }
        validateLineFeed(inputStream);
        return convertToString(line);
    }

    private static String convertLineOrNull(ByteArrayOutputStream line) {
        if (line.size() == 0) {
            return null;
        }
        return convertToString(line);
    }

    private static void validateLineFeed(InputStream inputStream) throws IOException {
        if (inputStream.read() != '\n') {
            throw new IOException("잘못된 HTTP 줄바꿈입니다.");
        }
    }

    private static String convertToString(ByteArrayOutputStream line) {
        return line.toString(StandardCharsets.ISO_8859_1);
    }

    private static Map<String, String> readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = readLine(inputStream)).isEmpty()) {
            String[] header = line.split(":", 2);
            headers.put(header[0].strip(), header[1].strip());
        }
        return Collections.unmodifiableMap(headers);
    }

    private static HttpParameters readBody(
            Map<String, String> headers, InputStream inputStream) throws IOException {
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            byte[] body = inputStream.readNBytes(contentLength);
            validateBodyLength(body, contentLength);
            String requestBody = new String(body, StandardCharsets.UTF_8);
            return HttpParameters.from(requestBody);
        }
        return HttpParameters.empty();
    }

    private static void validateBodyLength(byte[] body, int contentLength) throws IOException {
        if (body.length != contentLength) {
            throw new IOException("요청 본문의 길이가 Content-Length와 일치하지 않습니다.");
        }
    }
}
