package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public record HttpRequestBody(
        String requestBody
) {
    private static final HttpRequestBody EMPTY = new HttpRequestBody("");

    public static HttpRequestBody empty() {
        return EMPTY;
    }

    public static HttpRequestBody from(BufferedReader br, Integer contentLength) throws IOException {
        String requestBody;
        char[] buffer = new char[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            int result = br.read(buffer, offset, contentLength - offset);
            if (result == -1) {
                break;
            }
            offset += result;
        }
        requestBody = new String(buffer);

        return new HttpRequestBody(requestBody);
    }
}
