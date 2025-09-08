package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseBuilder {
    private static HttpStatus httpStatus;
    private static String responseBody;
    private static String contentType;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponseBuilder() {
    }

    public HttpResponseBuilder header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponseBuilder header(String key, int value) {
        headers.put(key, String.valueOf(value));
        return this;
    }

    public HttpResponseBuilder status(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder contentType(String value) {
        headers.put("Content-Type", value);
        return this;
    }

    public HttpResponseBuilder cookie(String key, String value) {
        headers.put("Set-Cookie", "%s=%s;".formatted(key, value));
        return this;
    }

    public HttpResponseBuilder body(String responseBody) {
        this.responseBody = responseBody;
        header("Content-Length", responseBody.getBytes(StandardCharsets.UTF_8).length);
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(httpStatus, headers, responseBody);
    }
}
