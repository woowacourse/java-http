package org.apache.coyote.http11.common;

import static org.apache.commons.lang3.StringUtils.trim;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum ContentType {

    ANY("*/*"),
    TEXT("text/plain"),
    JSON("application/json", "application/javascript", "text/javascript", "text/json"),
    XML("application/xml", "text/xml", "application/xhtml+xml"),
    HTML("text/html"),
    CSS("text/css"),
    URLENC("application/x-www-form-urlencoded"),
    BINARY("application/octet-stream"),
    MULTIPART("multipart/form-data", "multipart/alternative", "multipart/byteranges", "multipart/digest",
            "multipart/mixed", "multipart/parallel", "multipart/related", "multipart/report", "multipart/signed",
            "multipart/encrypted");

    private final String[] contentTypeStrings;

    ContentType(final String... contentTypeStrings) {
        this.contentTypeStrings = contentTypeStrings;
    }

    public static String withCharset(final String contentTypeString) {

        return withCharset(contentTypeString, "utf-8");
    }

    public static String withCharset(final String contentTypeString, final String charset) {
        validate(contentTypeString);

        if (StringUtils.isBlank(charset)) {
            throw new IllegalArgumentException("charset cannot be empty");
        }

        return String.format("%s;charset=%s", contentTypeString, trim(charset));
    }

    public static void validate(final String contentTypeStrings) {
        final var invalidStrings = Arrays.stream(contentTypeStrings.split(","))
                .noneMatch(ContentType::isContentType);

        if (invalidStrings) {
            throw new IllegalArgumentException("invalid content type string");
        }
    }

    private static boolean isContentType(final String contentTypeString) {
        return Arrays.stream(values())
                .anyMatch(contentType -> contentType.matches(contentTypeString));
    }

    private boolean matches(final String contentTypeString) {
        if (contentTypeString == null) {
            return false;
        }

        return Arrays.stream(this.contentTypeStrings)
                .anyMatch(contentType -> contentType.equalsIgnoreCase(contentTypeString.trim()));
    }

    @Override
    public String toString() {
        return contentTypeStrings[0];
    }
}
