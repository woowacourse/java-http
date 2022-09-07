package org.apache.coyote.http11;

import java.util.function.Function;

public enum StatusCode {

    OK("200", Http11Response::getOkResponse),
    FOUND("302", Http11Response::getFoundResponse);

    private final String value;
    private final Function<Http11Response, String> responseContentExtractor;

    StatusCode(String value, Function<Http11Response, String> responseContentExtractor) {
        this.value = value;
        this.responseContentExtractor = responseContentExtractor;
    }

    public String responseToString(Http11Response response) {
        return this.responseContentExtractor.apply(response);
    }

    public CharSequence statusCodeToString() {
        return String.format("%s %s", value, name());
    }
}
