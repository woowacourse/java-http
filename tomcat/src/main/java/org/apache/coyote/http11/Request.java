package org.apache.coyote.http11;

import java.util.Map;

public record Request(
        String method,
        String path,
        Map<String, String> headers,
        Map<String, String> params,
        Map<String, String> body,
        HttpCookie cookie
) {

    public Request(String method, String path, Map<String, String> headers,
                   Map<String, String> params, Map<String, String> body) {
        this(method, path, headers, params, body, new HttpCookie(headers.get("cookie")));
    }
}
