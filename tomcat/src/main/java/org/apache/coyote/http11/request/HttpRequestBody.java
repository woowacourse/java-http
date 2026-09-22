package org.apache.coyote.http11.request;

public record HttpRequestBody(
        String requestBody
) {
    private static final HttpRequestBody EMPTY = new HttpRequestBody("");

    public static HttpRequestBody empty() {
        return EMPTY;
    }
}
