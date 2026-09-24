package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK";
    private static final String REDIRECT_STATUS_LINE = "HTTP/1.1 302 Found";
    private static final String NOT_FOUND_STATUS_LINE = "HTTP/1.1 404 Not Found";

    private final String statusLine;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final String body;

    public HttpResponse(final String statusLine, final String body) {
        this.statusLine = statusLine;
        this.body = body;
    }

    public static HttpResponse createOkResponse(
            String contentType,
            String body,
            Map<String, String> responseHeaders) {
        HttpResponse httpResponse = new HttpResponse(OK_STATUS_LINE, body);
        responseHeaders.forEach(httpResponse::addHeader);
        httpResponse.addHeader("Content-Type", contentType);
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        return httpResponse;
    }

    public static HttpResponse createRedirectResponse(
            String redirectPath,
            Map<String, String> responseHeaders) {
        HttpResponse httpResponse = new HttpResponse(REDIRECT_STATUS_LINE, "");
        httpResponse.addHeader("Location", redirectPath);
        responseHeaders.forEach(httpResponse::addHeader);
        httpResponse.addHeader("Content-Length", "0");
        return httpResponse;
    }

    public static HttpResponse createNotFoundResponse(String body) {
        HttpResponse httpResponse = new HttpResponse(NOT_FOUND_STATUS_LINE, body);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        return httpResponse;
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder(statusLine);
        headers.forEach((name, value) ->
                response.append("\r\n")
                        .append(name)
                        .append(": ")
                        .append(value)
        );
        response.append("\r\n\r\n").append(body);
        return response.toString();
    }
}
