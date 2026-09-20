package org.apache.coyote.http11;

public record Route(
    HttpMethod httpMethod,
    String path
) {

    public static Route from(final Request request) {
        return new Route(request.httpMethod(), request.path());
    }
}
