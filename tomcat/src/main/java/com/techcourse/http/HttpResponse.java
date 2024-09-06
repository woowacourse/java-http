package com.techcourse.http;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String HEADER_SEPARATOR = ": ";

    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public static HttpResponse ok(String body) {
        return new HttpResponse(200, "OK", new HashMap<>(), body);
    }

    public static HttpResponse found(String location) {
        return new HttpResponse(302, "Found", Map.of(
                "Location", location
        ), "");
    }

    public static HttpResponse notFound() {
        return new HttpResponse(404, "Not Found", new HashMap<>(), "");
    }

    public static HttpResponse internalServerError() {
        return new HttpResponse(500, "Internal Server Error", new HashMap<>(), "");
    }

    public String build() {
        if (body.isBlank()) {
            return "%s %d %s \r\n%s\r\n"
                    .formatted(HTTP_VERSION, statusCode, statusMessage, getHeadersString());
        }

        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        return "%s %d %s \r\n%s\r\n%s"
                .formatted(HTTP_VERSION, statusCode, statusMessage, getHeadersString(), body);
    }

    private String getHeadersString() {
        StringBuilder headersString = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headersString.append(entry.getKey())
                    .append(HEADER_SEPARATOR)
                    .append(entry.getValue())
                    .append(" ")
                    .append(CRLF);
        }
        return headersString.toString();
    }

    public HttpResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public HttpResponse setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        headers.put("Content-Type", contentType);
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }
}
