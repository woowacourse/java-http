package org.apache.http;

public enum ContentType {

    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JS("application/javascript"),
    ICO("image/x-icon");

    private static final ContentType[] VALUES;

    static {
        VALUES = values();
    }

    ContentType(final String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return this.value;
    }

    public static ContentType of(final String fileExtension) {
        final ContentType contentType = resolve(fileExtension);
        if (contentType == null) {
            throw new IllegalArgumentException();
        }
        return contentType;
    }

    private static ContentType resolve(final String fileExtension) {
        for (final ContentType contentType : VALUES) {
            if (contentType.name().equalsIgnoreCase(fileExtension)) {
                return contentType;
            }
        }
        return null;
    }
}
