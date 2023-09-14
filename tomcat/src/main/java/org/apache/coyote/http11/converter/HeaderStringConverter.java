package org.apache.coyote.http11.converter;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.header.HeaderName;
import org.apache.coyote.http11.response.ContentType;

public class HeaderStringConverter {

    private static final String HEADER_DELIMITER = ": ";
    private static final String COOKIE_VALUE_DELIMITER = "=";

    private HeaderStringConverter() {

    }

    public static String convert(final String headerName, final String headerValue) {
        return headerName + HEADER_DELIMITER + headerValue + " ";
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
        final String headerMessage = cookieValues.entrySet().stream()
            .map(entry ->
                {
                    final String cookieValue =
                        entry.getKey() + COOKIE_VALUE_DELIMITER + entry.getValue();

                    return HeaderName.SET_COOKIE + HEADER_DELIMITER + cookieValue + " ";
                }
            )
            .collect(Collectors.joining(System.lineSeparator()));
        return headerMessage + System.lineSeparator();
    }
}
