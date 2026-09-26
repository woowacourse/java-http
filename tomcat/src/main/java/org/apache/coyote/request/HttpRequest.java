package org.apache.coyote.request;


import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public static final String CRLF = "\r\n";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    public static HttpRequest parse(String requestHead, String body) {
        String[] requestLines = requestHead.split(CRLF);

        RequestLine requestLine = RequestLine.parse(requestLines[0]);

        Map<String, String> headers = parseHeader(requestLines);

        return new HttpRequest(requestLine, headers, body);
    }

    private static Map<String, String> parseHeader(String[] requestLines) {
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < requestLines.length; i++) {
            String header = requestLines[i];

            String[] keyAndValue = header.split(":", 2);
            if (keyAndValue.length != 2 || keyAndValue[0].isBlank()) {
                throw new IllegalArgumentException("헤더 형식이 올바르지 않습니다.");
            }
            headers.put(keyAndValue[0], keyAndValue[1].trim());
        }

        return headers;
    }

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public String getCookieLine() {
        return headers.getOrDefault("Cookie", null);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestTarget() {
        return requestLine.getRequestTarget();
    }
}
