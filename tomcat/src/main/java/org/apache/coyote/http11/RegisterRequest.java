package org.apache.coyote.http11;

public record RegisterRequest(
    String account,
    String password,
    String email
) {

}
