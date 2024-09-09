package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static HttpRequest from(BufferedReader request) throws IOException {
        RequestLine requestLine = RequestLine.from(request.readLine());
        Map<String, String> headers = parseHeaders(request);
        parseRequestBody(request, headers);

        return new HttpRequest(requestLine, headers);
    }

    private static Map<String, String> parseHeaders(BufferedReader request) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = request.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }
        return headers;
    }

    private static void parseRequestBody(BufferedReader request, Map<String, String> headers) throws IOException {
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                request.read(buffer, 0, contentLength);
                headers.put("requestBody", new String(buffer));
            }
        }
    }

    public boolean containsCookie() {
        return headers.containsKey("Cookie");
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getCookie() {
        return headers.get("Cookie");
    }

    public String getRequestBody() {
        return headers.get("requestBody");
    }
}
