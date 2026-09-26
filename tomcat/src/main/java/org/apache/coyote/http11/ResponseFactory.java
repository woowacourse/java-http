package org.apache.coyote.http11;

import java.util.Optional;
import java.util.UUID;

final class ResponseFactory {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String LOCATION_HEADER = "Location";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";

    private ResponseFactory() {
    }

    static HttpResponse resourceResponse(
            final HttpStatus status,
            final byte[] body,
            final String contentType,
            final HttpRequest request
    ) {
        final var response = withNewSessionCookie(HttpResponse.of(status, body), request);

        return response
                .withHeader(CONTENT_TYPE_HEADER, contentType + " ")
                .withHeader(CONTENT_LENGTH_HEADER, body.length + " ");
    }

    static HttpResponse redirect(final String location, final HttpRequest request) {
        return redirect(location, newSessionIdFor(request));
    }

    static HttpResponse redirect(final String location, final Optional<String> sessionId) {
        final var response = sessionId
                .map(value -> HttpResponse.of(HttpStatus.FOUND, new byte[0])
                        .withHeader(SET_COOKIE_HEADER, SESSION_ID_COOKIE_NAME + "=" + value))
                .orElseGet(() -> HttpResponse.of(HttpStatus.FOUND, new byte[0]));

        return response
                .withHeader(LOCATION_HEADER, location)
                .withHeader(CONTENT_LENGTH_HEADER, "0");
    }

    static Optional<String> sessionId(final HttpRequest request) {
        return HttpCookie.from(request.header("Cookie")).value(SESSION_ID_COOKIE_NAME);
    }

    private static HttpResponse withNewSessionCookie(final HttpResponse response, final HttpRequest request) {
        return newSessionIdFor(request)
                .map(value -> response.withHeader(SET_COOKIE_HEADER, SESSION_ID_COOKIE_NAME + "=" + value))
                .orElse(response);
    }

    private static Optional<String> newSessionIdFor(final HttpRequest request) {
        if (sessionId(request).isPresent()) {
            return Optional.empty();
        }

        return Optional.of(UUID.randomUUID().toString());
    }
}
