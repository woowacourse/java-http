package org.apache.coyote.http11.common;

import static org.apache.commons.lang3.StringUtils.trim;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum MimeType {

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

    private final String[] mimeTypeStrings;

    MimeType(final String... mimeTypeStrings) {
        this.mimeTypeStrings = mimeTypeStrings;
    }

    public static String withCharset(final String mimeTypeString) {

        return withCharset(mimeTypeString, "utf-8");
    }

    public static String withCharset(final String mimeTypeString, final String charset) {
        validate(mimeTypeString);

        if (StringUtils.isBlank(charset)) {
            throw new IllegalArgumentException("charset cannot be empty");
        }

        return String.format("%s;charset=%s", mimeTypeString, trim(charset));
    }

    public static void validate(final String mimeTypeStrings) {
        final var invalidStrings = Arrays.stream(mimeTypeStrings.split(","))
                .noneMatch(MimeType::isContentType);

        if (invalidStrings) {
            throw new IllegalArgumentException("invalid content type string");
        }
    }

    private static boolean isContentType(final String mimeTypeString) {
        return Arrays.stream(values())
                .anyMatch(mimeType -> mimeType.matches(mimeTypeString));
    }

    private boolean matches(final String mimeTypeString) {
        if (mimeTypeString == null) {
            return false;
        }

        return Arrays.stream(this.mimeTypeStrings)
                .anyMatch(mimeType -> mimeType.equalsIgnoreCase(mimeTypeString.trim()));
    }

    @Override
    public String toString() {
        return mimeTypeStrings[0];
    }
}
