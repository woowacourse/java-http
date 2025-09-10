package org.apache.coyote.http11.request;

public record Api(
    HttpMethod method,
    String path
) {
}
