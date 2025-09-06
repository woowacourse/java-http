package org.apache.coyote.dto;

import org.apache.coyote.http11.MimeType;

public record ResourceResult(
        boolean found,
        String mimeType,
        String body
) {
    public static ResourceResult found(final String mimeType, final String body) {
        return new ResourceResult(true, mimeType, body);
    }

    public static ResourceResult notFound() {
        return new ResourceResult(false, MimeType.TXT.mimeType(), "Not found");
    }
}
