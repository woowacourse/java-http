package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers;
    private final String body;


    private HttpRequest(final String method,
                        final String uri,
                        final String version,
                        final Map<String, String> headers,
                        final  String body) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final String[] requestParts = requestLine.split(" ");
        final String httpMethod = requestParts[0];
        final String uri = resolveDefaultPage(requestParts[1]);
        final String version = requestParts[2];
        final Map<String, String> headers = collectHeaders(bufferedReader);

        if (httpMethod.equals("POST")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            return new HttpRequest(httpMethod, uri, version, headers, requestBody);
        }
        return new HttpRequest(httpMethod, uri, version, headers, null);
    }

    private static Map<String, String> collectHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isBlank()) {
            final String[] headerPair = requestLine.split(": ");
            headers.put(headerPair[0], headerPair[1]);
        }
        return headers;
    }

    private static String resolveDefaultPage(final String endPoint) {
        if (endPoint.equals("/") || endPoint.isEmpty()) {
            return "/index.html";
        }
        return endPoint;
    }

    public Cookie getCookie() {
        final String cookie = headers.get("Cookie");
        if (cookie != null) {
            return new Cookie(cookie);
        }
        return null;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getAcceptLine() {
        return headers.get("Accept");
    }

    public String getBody() {
        return body;
    }

    public String getVersion() {
        return version;
    }
}
