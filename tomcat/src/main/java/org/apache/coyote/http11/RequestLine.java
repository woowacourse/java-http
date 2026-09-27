package org.apache.coyote.http11;

public record RequestLine(
        HttpMethod method,
        RequestTarget target,
        HttpVersion version
) {

    private static final int ELEMENT_COUNT = 3;

    public static RequestLine from(final String value) {
        String[] elements = value.trim().split("\\s+");

        if (elements.length != ELEMENT_COUNT) {
            throw new IllegalArgumentException("올바르지 않은 Request Line: " + value);
        }

        return new RequestLine(
                HttpMethod.from(elements[0]),
                new RequestTarget(elements[1]),
                HttpVersion.from(elements[2])
        );
    }
}
