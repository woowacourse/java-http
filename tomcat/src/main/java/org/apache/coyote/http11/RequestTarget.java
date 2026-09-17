package org.apache.coyote.http11;

public record RequestTarget(
        String path,
        String queryString
) {
}
