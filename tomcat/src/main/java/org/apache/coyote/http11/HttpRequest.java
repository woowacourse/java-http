package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
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

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        Map<String, String> headers = new LinkedHashMap<>();
        readHeaders(bufferedReader, headers);

        final int contentLength = Integer.parseInt(
                headers.getOrDefault(CONTENT_LENGTH_HEADER, DEFAULT_CONTENT_LENGTH)
        );
        String requestBody = "";
        requestBody = readBody(bufferedReader, contentLength, requestBody);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static String readBody(BufferedReader bufferedReader, int contentLength, String requestBody)
            throws IOException {
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int totalRead = 0;
            readBodyLines(bufferedReader, contentLength, totalRead, buffer);
            requestBody = new String(buffer);
        }
        return requestBody;
    }

    private static void readBodyLines(BufferedReader bufferedReader, int contentLength, int totalRead, char[] buffer)
            throws IOException {
        while (totalRead < contentLength) {
            int count = bufferedReader.read(buffer, totalRead, contentLength - totalRead);
            if (count == -1) {
                throw new IOException(UNEXPECTED_END_OF_BODY_MESSAGE);
            }
            totalRead += count;
        }
    }

    private static void readHeaders(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            String[] parts = line.split(HEADER_DELIMITER, 2);
            headers.put(parts[0], parts[1].trim());
        }
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
