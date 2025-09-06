package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private final HttpVersion httpVersion;
    private final ResponseStatus responseStatus;
    private final ContentType contentType;
    private final long contentLength;
    private final byte[] body;

    public HttpResponse(HttpVersion httpVersion, ResponseStatus responseStatus, ContentType contentType,
                        long contentLength, byte[] body) {
        this.httpVersion = httpVersion;
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.body = body;
    }

    public static HttpResponse of(ResponseStatus responseStatus, ContentType contentType, byte[] body) {
        return new HttpResponse(
                HttpVersion.HTTP11,
                responseStatus,
                contentType,
                body.length,
                body);
    }

    public byte[] getBytes() {
        final var headers = String.join("\r\n",
                httpVersion.getResponseHeader() + " " + responseStatus.getResponseHeader() + " ",
                contentType.getResponseHeader() + " ",
                "Content-Length: " + contentLength + " ",
                "");
        final var bodyString = new String(body, StandardCharsets.UTF_8);
        final String fullResponse = headers + "\r\n" + bodyString;
        return fullResponse.getBytes();
    }
}
