package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final String statusLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(String statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.body = body == null ? "" : body;

        Map<String, String> copiedHeaders = new LinkedHashMap<>(headers);
        int contentLength = this.body.getBytes(StandardCharsets.UTF_8).length;
        copiedHeaders.put("Content-Length", String.valueOf(contentLength));

        this.headers = Map.copyOf(copiedHeaders);
    }

    public static HttpResponse createSuccessResponse(Map<String, String> headers, String contentType, String body) {
        Map<String, String> copiedHeaders = new LinkedHashMap<>(headers);
        copiedHeaders.put("Content-Type", contentType);
        return new HttpResponse(PROTOCOL_VERSION + " 200 OK", copiedHeaders, body);
    }

    public static HttpResponse createRedirectResponse(Map<String, String> headers, String redirectPath) {
        Map<String, String> copiedHeaders = new LinkedHashMap<>(headers);
        copiedHeaders.put("Location", redirectPath);
        return new HttpResponse(PROTOCOL_VERSION + " 302 Found", copiedHeaders, "");
    }

    public static HttpResponse createNotFoundResponse(Map<String, String> headers) {
        Map<String, String> copiedHeaders = new LinkedHashMap<>(headers);
        copiedHeaders.put("Content-Type", "text/plain;charset=utf-8");
        return new HttpResponse(PROTOCOL_VERSION + " 404 Not Found", copiedHeaders, "Not Found");
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
        response.append(body);
        return response.toString();
    }
}
