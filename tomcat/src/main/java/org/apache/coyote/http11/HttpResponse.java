package org.apache.coyote.http11;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.Cookie;
import org.apache.coyote.http11.header.HttpStatus;
import org.apache.coyote.http11.header.HttpVersion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private static final String HEADER_SEPARATOR = ": ";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private HttpStatus status;
    private String body;

    public HttpResponse addStatus(final HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpResponse addContentType(final ContentType contentType) {
        headers.put("Content-Type", contentType.getType());
        return this;
    }

    public HttpResponse addLocation(final String location) {
        headers.put("Location", location);
        return this;
    }

    public String build() {
        List<String> response = new ArrayList<>();
        response.add(HttpVersion.HTTP_1_1.getVersion() + " " + status.getStatusResponse() + " ");
        headers.forEach((key, value) -> response.add(key + HEADER_SEPARATOR + value + " "));
        response.add("");
        response.add(body);
        return String.join("\r\n", response);
    }

    public HttpResponse addSetCookie(final Cookie cookie) {
        headers.put("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
        return this;
    }

    public HttpResponse addBody(final String responseBody) {
        addContentLength(responseBody.getBytes().length);
        this.body = responseBody;
        return this;
    }

    public HttpResponse addContentLength(final int length) {
        headers.put("Content-Length", String.valueOf(length));
        return this;
    }

    public int getStatus() {
        return status.getCode();
    }
}
