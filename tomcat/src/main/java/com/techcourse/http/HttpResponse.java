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
    private HttpCookie cookie;
    private String body;

    private HttpResponse(int statusCode, String statusMessage, String body) {
        this(statusCode, statusMessage, new HashMap<>(), new HttpCookie(), body);
    }

    private HttpResponse(int statusCode, String statusMessage) {
        this(statusCode, statusMessage, "");
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse(200, "OK", body);
    }

    public static HttpResponse found(String location) {
        return new HttpResponse(302, "Found").setHeader("Location", location);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(404, "Not Found");
    }

    public static HttpResponse internalServerError() {
        return new HttpResponse(500, "Internal Server Error");
    }

    public String build() {
        if (cookie.isExist()) {
            headers.put("Set-Cookie", cookie.serialize());
        }
        if (!body.isBlank()) {
            headers.put("Content-Length", String.valueOf(body.getBytes().length));
        }

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

    public HttpResponse setCookie(String key, String value) {
        if (headers.get("Set-Cookie") != null) {
            headers.put("Set-Cookie", "%s; %s=%s".formatted(headers.get("Set-Cookie"), key, value));
            return this;
        }
        headers.put("Set-Cookie", "%s=%s".formatted(key, value));
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }
}
