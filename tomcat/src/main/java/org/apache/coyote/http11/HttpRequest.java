package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {
    private static final String COOKIE_HEADER = "Cookie";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String DEFAULT_CONTENT_LENGTH = "0";
    private static final String HEADER_DELIMITER = ":";
    private static final String UNEXPECTED_END_OF_BODY_MESSAGE = "예상보다 짧음";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final HttpCookie cookie;
    private final boolean shouldIssueSessionCookie;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.cookie = HttpCookie.create(headers.getOrDefault(COOKIE_HEADER, ""));
        this.shouldIssueSessionCookie = !cookie.isSessionId();
        if (shouldIssueSessionCookie) {
            cookie.setSessionId();
        }
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        RequestLine requestLine = RequestLine.from(readLine(inputStream));
        Map<String, String> headers = new LinkedHashMap<>();
        readHeaders(inputStream, headers);

        int contentLength = Integer.parseInt(
                headers.getOrDefault(CONTENT_LENGTH_HEADER, DEFAULT_CONTENT_LENGTH)
        );
        String requestBody = readBody(inputStream, contentLength);
        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static String readBody(InputStream inputStream, int contentLength) throws IOException {
        if (contentLength < 0) {
            throw new IOException("Content-Length는 음수일 수 없습니다.");
        }
        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IOException(UNEXPECTED_END_OF_BODY_MESSAGE);
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    private static void readHeaders(InputStream inputStream, Map<String, String> headers) throws IOException {
        while (true) {
            String line = readLine(inputStream);
            if (line == null) {
                throw new IOException("요청 헤더가 끝나기 전에 입력이 종료되었습니다.");
            }
            if (line.isEmpty()) {
                break;
            }
            String[] parts = line.split(HEADER_DELIMITER, 2);
            headers.put(parts[0], parts[1].trim());
        }
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1) {
            if (value == '\n') {
                break;
            }
            buffer.write(value);
        }
        if (value == -1 && buffer.size() == 0) {
            return null;
        }
        String line = buffer.toString(StandardCharsets.ISO_8859_1);
        return line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Uri getUri() {
        return requestLine.getUri();
    }

    public String getHeader(String name) {
        if (headers.containsKey(name)) {
            return headers.get(name);
        }
        return "";
    }

    public String getBody() {
        return body;
    }

    public String getSessionId() {
        return cookie.getSessionId();
    }

    HttpCookie getCookie() {
        return cookie;
    }

    boolean shouldIssueSessionCookie() {
        return shouldIssueSessionCookie;
    }
}
