package org.apache.coyote.dto;

public record ResourceResult(
        boolean found,
        String body
) {
    public static ResourceResult found(final String body) {
        return new ResourceResult(true, body);
    }

    public static ResourceResult notFound() {
        return new ResourceResult(false, "");
    }
}
