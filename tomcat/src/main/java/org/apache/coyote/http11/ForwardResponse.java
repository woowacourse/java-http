package org.apache.coyote.http11;

public record ForwardResponse(HttpStatusCode httpStatusCode, String resourcePath) {
}
