package org.apache.coyote.http11.response;

public record HttpResponseBody(
        String responseBody
) {
    private static final HttpResponseBody EMPTY = new HttpResponseBody("");

    public static HttpResponseBody empty() {
        return EMPTY;
    }

    public int length() {
        return responseBody.getBytes().length;
    }

    public String format() {
        return responseBody;
    }
}
