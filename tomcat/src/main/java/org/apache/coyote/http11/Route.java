package org.apache.coyote.http11;

public record Route(HttpMethod httpMethod, String path) {
}
