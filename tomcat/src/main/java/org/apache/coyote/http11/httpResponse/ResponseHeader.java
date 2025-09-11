package org.apache.coyote.http11.httpResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.httpRequest.HttpCookie;

public class ResponseHeader {

    private final Map<String, List<String>> headers;

    private ResponseHeader(final Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public static ResponseHeader defaultOf(final String body) {
        final Map<String, List<String>> headers = new LinkedHashMap<>();

        headers.put("Content-Type", List.of(ContentType.HTML.getContentType() + ";charset=utf-8"));
        headers.put("Content-Length", List.of(String.valueOf(body.getBytes(StandardCharsets.UTF_8).length)));

        return new ResponseHeader(headers);
    }

    public ResponseHeader build(final String body) {
        this.headers.put("Content-Length", List.of(String.valueOf(body.getBytes(StandardCharsets.UTF_8).length)));

        return this;
    }

    public ResponseHeader build(
            final String body,
            final String type
    ) {
        String contentType = type;
        if (contentType.startsWith("text/")) {
            contentType += ";charset=utf-8";
        }
        this.headers.put("Content-Type", List.of(contentType));
        this.headers.put("Content-Length", List.of(String.valueOf(body.getBytes(StandardCharsets.UTF_8).length)));

        return this;
    }

    public ResponseHeader location(final String location) {
        this.headers.put("Location", List.of(location));

        return this;
    }

    public ResponseHeader setCookie(final HttpCookie httpCookie) {
        final Map<String, String> cookies = httpCookie.getCookies();

        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.headers.computeIfAbsent("Set-Cookie", ignored -> new ArrayList<>())
                    .add(entry.getKey() + "=" + entry.getValue());
        }

        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }
}
