package org.apache.coyote.http11.dto;

public record HttpRequestUrl(
        String method,
        String path,
        String version,
        String queryString
) {
}
