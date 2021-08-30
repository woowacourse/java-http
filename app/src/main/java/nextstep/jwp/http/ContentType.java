package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentType {
    JS, SVG, CSS;

    public static boolean matches(final String contentTypeHeader) {
        return Arrays.stream(values())
                .anyMatch(contentType -> contentTypeHeader.toLowerCase().contains(contentType.name().toLowerCase()));
    }
}
