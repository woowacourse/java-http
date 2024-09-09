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

    private HttpStatusCode httpStatusCode;
    private Map<String, String> headers;
    private HttpCookie cookie;
    private String body;

    private HttpResponse(HttpStatusCode statusCode, String body) {
        this(statusCode, new HashMap<>(), new HttpCookie(), body);
    }

    private HttpResponse(int statusCode, String body) {
        this(HttpStatusCode.from(statusCode), new HashMap<>(), new HttpCookie(), body);
    }

    private HttpResponse(HttpStatusCode statusCode) {
        this(statusCode, "");
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse(HttpStatusCode.OK, body);
    }

    public static HttpResponse found(String location) {
        return new HttpResponse(HttpStatusCode.FOUND).setHeader("Location", location);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(HttpStatusCode.NOT_FOUND);
    }

    public static HttpResponse internalServerError() {
        return new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    public String build() {
        if (cookie.isExist()) {
            headers.put("Set-Cookie", cookie.serialize());
        }
        if (!body.isBlank()) {
            headers.put("Content-Length", String.valueOf(body.getBytes().length));
        }

        return "%s %d %s \r\n%s\r\n%s"
                .formatted(HTTP_VERSION, httpStatusCode.getCode(), httpStatusCode.getMessage(), getHeadersString(), body);
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
