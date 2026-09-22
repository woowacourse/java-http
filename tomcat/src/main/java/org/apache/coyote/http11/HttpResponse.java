package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {

    private final String statusLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(String statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse createSuccessResponse(Map<String, String> headers, String body) {
        return new HttpResponse("HTTP/1.1 200 OK", headers, body);
    }

    public static HttpResponse createRedirectResponse(Map<String, String> headers, String body) {
        return new HttpResponse("HTTP/1.1 302 Found", headers, body);
    }

    public static HttpResponse createNotFoundResponse(Map<String, String> headers, String body) {
        return new HttpResponse("HTTP/1.1 404 Not Found", headers, body);
    }

    public String toResponse() {
        StringBuilder response = new StringBuilder();
        response.append(statusLine).append("\r\n");
        headers.forEach((key, value) ->
                response.append(key)
                        .append(": ")
                        .append(value)
                        .append("\r\n"));

        response.append("\r\n");
        if (body != null) {
            response.append(body);
        }
        return response.toString();
    }
}
