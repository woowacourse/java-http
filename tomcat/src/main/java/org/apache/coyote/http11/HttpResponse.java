package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final HttpVersion httpVersion;
    private final ResponseStatus responseStatus;
    private final ContentType contentType;
    private final String location;
    private final long contentLength;
    private final byte[] body;

    public HttpResponse(HttpVersion httpVersion,
                        ResponseStatus responseStatus,
                        ContentType contentType,
                        String location,
                        long contentLength,
                        byte[] body) {
        this.httpVersion = httpVersion;
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.location = location;
        this.contentLength = contentLength;
        this.body = body;
    }

    public static HttpResponse of(ResponseStatus responseStatus, ContentType contentType, byte[] body) {
        return new HttpResponse(
                HttpVersion.HTTP11,
                responseStatus,
                contentType,
                null,
                body.length,
                body);
    }

    public static HttpResponse forRedirect(ResponseStatus responseStatus, String location) {
        return new HttpResponse(
                HttpVersion.HTTP11,
                responseStatus,
                ContentType.HTML,
                location,
                0L,
                new byte[0]);
    }

    public byte[] getBytes() {
        final var headers = getHeaders();
        final var bodyString = new String(body, StandardCharsets.UTF_8);
        final String fullResponse = headers + bodyString;
        return fullResponse.getBytes();
    }

    private String getHeaders() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStatusLine()).append(CRLF);
        if (location != null && !location.isBlank()) {
            sb.append("Location: ").append(location).append(CRLF);
        }
        if (contentType != null) {
            sb.append(contentType.getResponseHeader()).append(CRLF);
        }
        sb.append("Content-Length: ").append(contentLength).append(CRLF);
        sb.append(CRLF);
        return sb.toString();
    }

    private String getStatusLine() {
        return httpVersion.getResponseHeader() + " " + responseStatus.getResponseHeader();
    }
}
