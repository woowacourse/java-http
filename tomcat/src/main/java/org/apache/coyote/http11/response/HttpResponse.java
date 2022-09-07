package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final StatusCode statusCode;
    private final HttpResponseHeader responseHeader;
    private final String body;

    public HttpResponse(final StatusCode statusCode,
                        final Map<String, String> responseHeader,
                        final String body) {
        this.statusCode = statusCode;
        this.responseHeader = new HttpResponseHeader(responseHeader);
        this.body = body;
    }

    public static HttpResponse ok(String resource, String body) {
        StatusCode statusCode = StatusCode.OK;
        ContentType contentType = ContentType.from(resource);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", contentType.getValue());
        return new HttpResponse(statusCode, header, body);
    }

    public static HttpResponse found(String resource, String body) {
        StatusCode statusCode = StatusCode.FOUND;
        ContentType contentType = ContentType.from(resource);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", contentType.getValue());
        return new HttpResponse(statusCode, header, body);
    }

    public static HttpResponse unauthorized(String resource, String body) {
        StatusCode statusCode = StatusCode.UNAUTHORIZED;
        ContentType contentType = ContentType.from(resource);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", contentType.getValue());
        return new HttpResponse(statusCode, header, body);
    }

    public static HttpResponse notFound() {
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", ContentType.HTML.getValue());
        return new HttpResponse(StatusCode.NOT_FOUND, header, FileReader.read("/401.html"));
    }

    public void setCookie(String key, String value) {
        responseHeader.add("Set-Cookie", key + "=" + value);
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
