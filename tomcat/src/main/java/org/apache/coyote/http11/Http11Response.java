package org.apache.coyote.http11;

public record Http11Response(int status, String contentType, int contentLength, String content) {

    private static final String DELIMITER = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_LENGTH = "Content-Length: ";

    public String getResponse() {
        return String.join(
                DELIMITER,
                HTTP_VERSION + status + " " + StatusCode.getStatusCode(status) + " ",
                CONTENT_TYPE + contentType,
                CONTENT_LENGTH + contentLength + " ",
                "",
                content);
    }
}
