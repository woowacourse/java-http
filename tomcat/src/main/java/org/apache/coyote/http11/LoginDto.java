package org.apache.coyote.http11;

public record LoginDto(
        String account,
        String password
) {
}
