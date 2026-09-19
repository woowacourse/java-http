package org.apache.coyote.http11;

public record Route(
    HttpMethod httpMethod,
    String path
) {

    public static Route from(final RequestTarget requestTarget) {
        return new Route(requestTarget.httpMethod(), requestTarget.path());
    }
}
