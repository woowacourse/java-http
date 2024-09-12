package org.apache.coyote;

public record ForwardResult(HttpStatusCode statusCode, String path) {

    public static ForwardResult ofRedirect(String path) {
        return new ForwardResult(HttpStatusCode.FOUND, path);
    }

    public boolean isRedirection() {
        return statusCode.isRedirection();
    }
}
