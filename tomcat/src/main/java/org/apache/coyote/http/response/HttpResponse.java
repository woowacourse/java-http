package org.apache.coyote.http.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpVersion;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private static final HttpVersion DEFAULT_VERSION = HttpVersion.HTTP_1_1;

    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private final ResponseBody body;

    private HttpResponse(HttpStatus status, Map<String, String> headers, ResponseBody body) {
        this.statusLine = StatusLine.of(DEFAULT_VERSION, status);
        this.headers = HttpHeaders.from(withContentHeaders(headers, body));
        this.body = body;
    }

    public static HttpResponse ok(ResponseBody body) {
        return of(HttpStatus.OK, body);
    }

    public static HttpResponse of(HttpStatus status, ResponseBody body) {
        return new HttpResponse(status, Map.of(), body);
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(HttpStatus.FOUND, Map.of("Location", location), EmptyBody.INSTANCE);
    }

    private static Map<String, String> withContentHeaders(Map<String, String> headers, ResponseBody body) {
        final Map<String, String> values = new LinkedHashMap<>();
        final int contentLength = body.bytes().length;
        if (contentLength > 0) {
            values.put(CONTENT_TYPE, body.contentType());
        }
        values.put(CONTENT_LENGTH, String.valueOf(contentLength));
        values.putAll(headers);
        return values;
    }

    public HttpResponse addCookie(String key, String value) {
        headers.setCookie(key, value);
        return this;
    }

    public StatusLine statusLine() {
        return statusLine;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public ResponseBody body() {
        return body;
    }
}
