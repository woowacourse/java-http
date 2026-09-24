package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public record HttpRequest(
        HttpRequestStartLine startLine,
        HttpRequestHeader requestHeader,
        HttpRequestBody requestBody
) {
    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequestStartLine startLine = HttpRequestStartLine.from(br);
        HttpRequestHeader requestHeader = HttpRequestHeader.from(br);

        Optional<Integer> contentLength = requestHeader.getContentLength();
        if (contentLength.isEmpty()) {
            return new HttpRequest(startLine, requestHeader, HttpRequestBody.empty());
        }
        HttpRequestBody requestBody = HttpRequestBody.from(br, contentLength.get());
        return new HttpRequest(startLine, requestHeader, requestBody);
    }
}
