package org.apache.coyote.http11.response;

import java.util.function.Function;

public enum StatusCode {

    OK("200", Http11Response::getOkResponse),
    FOUND("302", response -> {
        if (response.hasSetCookieHeader()) {
            return response.getFoundResponseWithSetCookie();
        }
        return response.getFoundResponse();
    });

    private final String statusMessage;
    private final Function<Http11Response, String> responseContentExtractor;

    StatusCode(String statusMessage, Function<Http11Response, String> responseContentExtractor) {
        this.statusMessage = statusMessage;
        this.responseContentExtractor = responseContentExtractor;
    }

    public String responseToString(final Http11Response response) {
        return this.responseContentExtractor.apply(response);
    }

    public CharSequence statusCodeToString() {
        return String.format("%s %s", statusMessage, name());
    }
}
