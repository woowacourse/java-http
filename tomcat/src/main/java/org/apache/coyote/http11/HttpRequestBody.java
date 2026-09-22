package org.apache.coyote.http11;

public record HttpRequestBody(
        String requestBody
) {
    private static final HttpRequestBody EMPTY = new HttpRequestBody("");

    public static HttpRequestBody empty() {
        return EMPTY;
    }
}
