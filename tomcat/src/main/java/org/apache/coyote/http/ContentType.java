package org.apache.coyote.http;

import java.util.Arrays;
import java.util.function.Function;

public enum ContentType {

    JSON("application/json", TextBody::new),
    TEXT_PLAIN("text/plain", TextBody::new),
    UNKNOWN("", TextBody::new);

    private final String mediaType;
    private final Function<String, RequestBody> bodyParser;

    ContentType(String mediaType, Function<String, RequestBody> bodyParser) {
        this.mediaType = mediaType;
        this.bodyParser = bodyParser;
    }

    public static ContentType from(String headerValue) {
        // "application/json; charset=UTF-8" → "application/json"
        final String mediaType = headerValue.split(";")[0].trim();
        return Arrays.stream(values())
                .filter(type -> type.mediaType.equalsIgnoreCase(mediaType))
                .findFirst()
                .orElse(UNKNOWN);
    }

    RequestBody parse(String rawBody) {
        return bodyParser.apply(rawBody);
    }
}
