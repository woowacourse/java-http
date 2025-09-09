package org.apache.coyote.http11;

public record LoginRequest(
    String account,
    String password
) {
}
