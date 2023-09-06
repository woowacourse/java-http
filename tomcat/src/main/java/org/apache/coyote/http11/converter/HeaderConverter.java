package org.apache.coyote.http11.converter;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.header.HeaderName;
import org.apache.coyote.http11.response.ContentType;

public class HeaderConverter {

    private static final String HEADER_DELIMITER = ": ";
    private static final String COOKIE_VALUE_DELIMITER = "=";

    private HeaderConverter() {

    }

    public static String toLocation(final String locationUrl) {
        return HeaderName.LOCATION + HEADER_DELIMITER + locationUrl;
    }

    public static String toContentType(final ContentType contentType) {
        return HeaderName.CONTENT_TYPE + HEADER_DELIMITER + contentType.getType();
    }

    public static String toContentLength(final String body) {
        return HeaderName.CONTENT_LENGTH + HEADER_DELIMITER + body.getBytes().length;
    }

    public static String toSetCookie(final Map<String, String> cookieValues) {
        return cookieValues.entrySet().stream()
            .map(entry ->
                {
                    final String cookieValue =
                        entry.getKey() + COOKIE_VALUE_DELIMITER + entry.getValue();

                    return HeaderName.SET_COOKIE + HEADER_DELIMITER + cookieValue;
                }
            )
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
