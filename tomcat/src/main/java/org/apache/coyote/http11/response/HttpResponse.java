package org.apache.coyote.http11.response;

import org.apache.coyote.http11.FileReader;

public class HttpResponse {

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String body;

    public HttpResponse(StatusCode statusCode, ContentType contentType, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse ok(String resource, String body) {
        StatusCode statusCode = StatusCode.OK;
        ContentType contentType = ContentType.from(resource);
        return new HttpResponse(statusCode, contentType, body);
    }

    public static HttpResponse found(String resource, String body) {
        StatusCode statusCode = StatusCode.FOUND;
        ContentType contentType = ContentType.from(resource);
        return new HttpResponse(statusCode, contentType, body);
    }

    public static HttpResponse unauthorized(String resource, String body) {
        StatusCode statusCode = StatusCode.UNAUTHORIZED;
        ContentType contentType = ContentType.from(resource);
        return new HttpResponse(statusCode, contentType, body);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(StatusCode.NOT_FOUND, ContentType.HTML, FileReader.read("/401.html"));
    }

    public String getMessage() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.getCode() + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
