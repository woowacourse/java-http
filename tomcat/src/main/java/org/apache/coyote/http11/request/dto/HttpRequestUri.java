package org.apache.coyote.http11.request.dto;

public record HttpRequestUri(
        String method,
        String path,
        String version,
        String queryString
) {
}
