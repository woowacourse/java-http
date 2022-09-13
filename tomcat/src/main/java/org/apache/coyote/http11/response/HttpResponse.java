package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.common.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final StatusCode statusCode;
    private final HttpResponseHeader responseHeader;
    private final String body;

    public HttpResponse(final StatusCode statusCode,
                        final HttpResponseHeader responseHeader,
                        final String body) {
        this.statusCode = statusCode;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public HttpResponse(final StatusCode statusCode, final HttpResponseHeader responseHeader) {
        this(statusCode, responseHeader, "");
    }

    public static HttpResponse ok(final String resource, final String body) {
        final StatusCode statusCode = StatusCode.OK;
        final ContentType contentType = ContentType.from(resource);
        final HttpResponseHeader responseHeader = HttpResponseHeader.fromContentType(contentType);
        return new HttpResponse(statusCode, responseHeader, body);
    }

    public static HttpResponse found(final String resource, final String body) {
        final StatusCode statusCode = StatusCode.FOUND;
        final ContentType contentType = ContentType.from(resource);
        final HttpResponseHeader responseHeader = HttpResponseHeader.fromContentType(contentType);
        return new HttpResponse(statusCode, responseHeader, body);
    }

    public static HttpResponse unauthorized() {
        final HttpResponseHeader responseHeader = HttpResponseHeader.fromContentType(ContentType.HTML);
        return new HttpResponse(StatusCode.UNAUTHORIZED, responseHeader, FileReader.read("/401.html"));
    }

    public static HttpResponse notFound() {
        final HttpResponseHeader responseHeader = HttpResponseHeader.fromContentType(ContentType.HTML);
        return new HttpResponse(StatusCode.NOT_FOUND, responseHeader, FileReader.read("/404.html"));
    }

    public static HttpResponse internalServerError() {
        final HttpResponseHeader responseHeader = HttpResponseHeader.fromContentType(ContentType.HTML);
        return new HttpResponse(StatusCode.INTERNAL_SERVER_ERROR, responseHeader, FileReader.read("/500.html"));
    }

    public static HttpResponse redirect(final StatusCode statusCode, final String resource) {
        final ContentType contentType = ContentType.from(resource);
        final HttpResponseHeader responseHeader = HttpResponseHeader.fromContentType(contentType);
        final HttpResponse httpResponse = new HttpResponse(statusCode, responseHeader);
        responseHeader.addLocation(resource);
        return httpResponse;
    }

    public void setCookie(String key, String value) {
        responseHeader.setCookie(key + "=" + value);
        log.info("setting-cookie: {}", key + "=" + value);
    }

    public String getMessage() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.getCode() + " ",
                responseHeader.getAll(),
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
