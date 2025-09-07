package org.apache.coyote.http11.dto;

import java.util.Collections;
import java.util.Map;

public record HttpRequest(
        String method,
        String route,
        Map<String, String> query,
        String protocol,
        Map<String, String> headers,
        HttpCookie httpCookie
) {

    public HttpRequest(
            final String method,
            final String route,
            final Map<String, String> query,
            final String protocol,
            final Map<String, String> headers,
            final HttpCookie httpCookie
    ) {
        this.method = method;
        this.route = route;
        this.query = Collections.unmodifiableMap(query);
        this.protocol = protocol;
        this.headers = Collections.unmodifiableMap(headers);
        this.httpCookie = httpCookie;
    }
}
