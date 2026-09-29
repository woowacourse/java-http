package org.apache.catalina;

import org.apache.coyote.http11.HttpStatus;

public record DispatchResult(
    DispatchType type,
    HttpStatus status,
    String path
) {

    public static DispatchResult forward(final HttpStatus status, final String path) {
        return new DispatchResult(DispatchType.FORWARD, status, path);
    }

    public static DispatchResult redirect(final HttpStatus status, final String path) {
        return new DispatchResult(DispatchType.REDIRECT, status, path);
    }

}
