package org.apache.coyote.http11.common;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.trim;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
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

    public static Optional<ContentType> from(final String contentTypeString) {
        if (Objects.isNull(contentTypeString)) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(contentType -> contentType.matches(contentTypeString))
                .findFirst();
    }

    private boolean matches(final String contentTypeString) {
        return Arrays.stream(this.contentTypeStrings)
                .anyMatch(contentType -> contentType.equalsIgnoreCase(contentTypeString.trim()));
    }

    public String[] getContentTypeStrings() {
        return contentTypeStrings;
    }

    public String withCharset(final String charset) {
        if (StringUtils.isBlank(charset)) {
            throw new IllegalArgumentException("charset cannot be empty");
        }
        return format("%s;charset=%s", this.toString(), trim(charset));
    }

    @Override
    public String toString() {
        return contentTypeStrings[0];
    }
}
