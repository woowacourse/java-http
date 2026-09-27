package org.apache.coyote.http11.request;

public record RequestTarget(String path, QueryParameters queryParameters) {
}
