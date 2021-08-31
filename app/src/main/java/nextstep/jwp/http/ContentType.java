package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentType {
    JS("text/css"),
    SVG("text/css"),
    CSS("text/css"),
    NOTHING("text/html");

    private final String contentTypeResponse;

    ContentType(String contentTypeResponse) {
        this.contentTypeResponse = contentTypeResponse;
    }

    public static ContentType matches(final String contentTypeHeader) {
        return Arrays.stream(values())
                .filter(contentType -> contentTypeHeader.toLowerCase().contains(contentType.name().toLowerCase()))
                .findAny()
                .orElse(NOTHING);
    }
}
