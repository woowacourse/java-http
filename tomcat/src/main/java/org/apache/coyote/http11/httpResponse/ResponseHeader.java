package org.apache.coyote.http11.httpResponse;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.httpRequest.HttpCookie;

public class ResponseHeader {

    private final Map<String, String> headers;

    private ResponseHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeader defaultOf(final String body) {
        final Map<String, String> headers = new LinkedHashMap<>();

        headers.put("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return new ResponseHeader(headers);
    }

    public ResponseHeader build(final String body) {
        this.headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return this;
    }

    public ResponseHeader build(
            final String body,
            final String contentType
    ) {
        this.headers.put("Content-Type", contentType + ";charset=utf-8");
        this.headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return this;
    }

    public ResponseHeader location(final String location) {
        this.headers.put("Location", location);

        return this;
    }

    public ResponseHeader setCookie(final HttpCookie httpCookie) {
        final Map<String, String> cookies = httpCookie.getCookies();
        final StringBuilder sb = new StringBuilder();
        for (String name : cookies.keySet()) {
            sb.append(name).append("=").append(cookies.get(name)).append(";");
        }

        this.headers.put("Set-Cookie", sb.toString());

        return this;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
