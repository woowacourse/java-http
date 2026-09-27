package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ":";
    private static final String FORM_DELIMITER = "&";
    private static final String COOKIE_DELIMITER = ";";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 비어 있습니다.");
        }

        Map<String, String> headers = readHeaders(bufferedReader);
        String body = readBody(headers, bufferedReader);
        return new HttpRequest(RequestLine.from(requestLine), headers, body);
    }

    private static Map<String, String> readHeaders(BufferedReader bufferedReader)
        throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] parsedLine = line.split(HEADER_DELIMITER, 2);
            if (parsedLine.length != 2) {
                continue;
            }

            headers.put(parsedLine[0].strip(), parsedLine[1].strip());
        }
        return headers;
    }

    private static String readBody(Map<String, String> headers, BufferedReader bufferedReader)
        throws IOException {
        String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return "";
        }

        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        int read = 0;
        while (read < length) {
            int count = bufferedReader.read(buffer, read, length - read);
            if (count == -1) {
                break;
            }
            read += count;
        }
        return new String(buffer, 0, read);
    }

    public boolean isMethod(String method) {
        return requestLine.getMethod().equals(method);
    }

    public boolean hasQueryString() {
        return requestLine.hasQueryString();
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

    public String getQueryParameter(String name) {
        return requestLine.getQueryParameter(name);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getFormData() {
        return KeyValueParser.parse(body, FORM_DELIMITER);
    }

    public String getCookie(String name) {
        String cookieHeader = headers.get(COOKIE);
        if (cookieHeader == null) {
            return null;
        }
        return KeyValueParser.parse(cookieHeader, COOKIE_DELIMITER).get(name);
    }
}
