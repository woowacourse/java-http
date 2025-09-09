package org.apache.coyote.http11;

import java.util.UUID;

public record Http11Response(
        HttpStatusCode statusCode,
        String contentType,
        byte[] content,
        String location,
        UUID sessionId) {

    private static final String DELIMITER = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_LENGTH = "Content-Length: ";
    private static final String LOCATION = "Location: ";
    private static final String SET_COOKIE = "Set-Cookie: JSESSIONID=";

    public String getResponseHeader() {
        StringBuilder sb = new StringBuilder();

        // 상태 라인
        sb.append(HTTP_VERSION)
                .append(statusCode.getStatus())
                .append(" ")
                .append(statusCode.getStatusCode())
                .append(DELIMITER);

        // Content-Type
        if (contentType != null) {
            sb.append(CONTENT_TYPE).append(contentType).append(DELIMITER);
        }

        // Content-Length
        int contentLength = (content != null ? content.length : 0);
        sb.append(CONTENT_LENGTH).append(contentLength).append(DELIMITER);

        // Location 헤더
        if (location != null) {
            sb.append(LOCATION).append(location).append(DELIMITER);
        }

        // Cookie 헤더
        if (sessionId != null) {
            sb.append(SET_COOKIE).append(sessionId).append(DELIMITER);
        }

        // 헤더 끝에 CRLF
        sb.append(DELIMITER);

        return sb.toString();
    }

    public byte[] getResponseBody() {
        return content;
    }
}
