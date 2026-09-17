package org.apache.coyote.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpServletResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final HttpStatus status;
    private final HttpHeaders headers;
    private final ResponseBody body;

    private HttpServletResponse(HttpStatus status, Map<String, String> headers, ResponseBody body) {
        this.status = status;
        this.headers = HttpHeaders.from(withContentHeaders(headers, body));
        this.body = body;
    }

    public static HttpServletResponse ok(ResponseBody body) {
        return of(HttpStatus.OK, body);
    }

    public static HttpServletResponse of(HttpStatus status, ResponseBody body) {
        return new HttpServletResponse(status, Map.of(), body);
    }

    public static HttpServletResponse redirect(String location) {
        return new HttpServletResponse(HttpStatus.FOUND, Map.of("Location", location), EmptyBody.INSTANCE);
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

    public HttpStatus status() { return status; }
    public HttpHeaders headers() { return headers; }
    public ResponseBody body() { return body; }
}
