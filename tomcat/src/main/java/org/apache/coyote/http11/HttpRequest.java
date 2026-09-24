package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

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
    private final Map<String, String> bodyParams;
    private Session session;

    private HttpRequest(final RequestLine requestLine,
                        final Map<String, String> headers, final Map<String, String> bodyParams) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.bodyParams = bodyParams;
    }

    public static HttpRequest from(final String requestLine) {
        return new HttpRequest(RequestLine.from(requestLine),
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER), new HashMap<>());
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final String requestLine = readLine(inputStream);
        if (requestLine == null) {
            throw new IOException("HTTP 요청 줄을 읽을 수 없습니다.");
        }
        final HttpRequest request = from(requestLine);

        String headerLine;
        while ((headerLine = readLine(inputStream)) != null && !headerLine.isEmpty()) {
            final String[] header = headerLine.split(":", 2);
            request.headers.put(header[0].trim(), header[1].trim());
        }
        request.bodyParams.putAll(readBodyParams(inputStream, request.headers));
        return request;
    }

    private static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int current;
        while ((current = inputStream.read()) != -1 && current != '\n') {
            buffer.write(current);
        }
        if (current == -1 && buffer.size() == 0) {
            return null;
        }

        final byte[] line = buffer.toByteArray();
        final int length = line.length > 0 && line[line.length - 1] == '\r'
                ? line.length - 1
                : line.length;
        return new String(line, 0, length, StandardCharsets.ISO_8859_1);
    }

    private static Map<String, String> readBodyParams(final InputStream inputStream,
                                                       final Map<String, String> headers) throws IOException {
        final String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return Map.of();
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final byte[] body = inputStream.readNBytes(contentLength);
        if (body.length < contentLength) {
            throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
        }
        return extractFormParams(new String(body, StandardCharsets.UTF_8));
    }

    private static Map<String, String> extractFormParams(final String requestBody) {
        final Map<String, String> params = new HashMap<>();
        for (String param : requestBody.split("&")) {
            final String[] keyAndValue = param.split("=", 2);
            final String key = URLDecoder.decode(keyAndValue[0], StandardCharsets.UTF_8);
            final String value = keyAndValue.length == 2
                    ? URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8)
                    : "";
            params.put(key, value);
        }
        return params;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public Map<String, String> getBodyParams() {
        return bodyParams;
    }

    public HttpCookie getCookies() {
        return HttpCookie.from(getHeader("Cookie"));
    }

    public Session getSession(final boolean create) {
        if (session != null) {
            return session;
        }

        final SessionManager sessionManager = SessionManager.getInstance();
        session = getCookies().getValue("JSESSIONID")
                .map(sessionManager::findSession)
                .orElse(null);

        if (session != null || !create) {
            return session;
        }

        session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session;
    }
}
