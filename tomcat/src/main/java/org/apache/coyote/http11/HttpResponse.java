package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private static final String HEADER_SEPARATOR = ": ";
    private final HttpStatus status;
    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponse(final HttpStatus status) {
        this.status = status;
    }

    public HttpResponse addHeader(final String key, final String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponse addContentType(final ContentType contentType) {
        headers.put("Content-Type", contentType.getType());
        return this;
    }

    public HttpResponse addContentLength(final int length) {
        headers.put("Content-Length", String.valueOf(length));
        return this;
    }

    public HttpResponse addCharset(final String charset) {
        final String type = headers.get("Content-Type") + ";charset=" + charset;
        headers.put("Content-Type", type);
        return this;
    }

    public HttpResponse addLocation(final String location) {
        headers.put("Location", location);
        return this;
    }

    public String build() {
        return build("");
    }

    public String build(final String body) {
        List<String> response = new ArrayList<>();
        response.add("HTTP/1.1 " + status.getStatusResponse() + " ");
        headers.forEach((key, value) -> response.add(key + HEADER_SEPARATOR + value + " "));
        response.add("");
        response.add(body);
        return String.join("\r\n", response);
    }

    public HttpResponse addSetCookie(final Cookie cookie) {
        headers.put("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
        return this;
    }
}
