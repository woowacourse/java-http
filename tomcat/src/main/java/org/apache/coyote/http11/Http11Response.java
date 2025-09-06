package org.apache.coyote.http11;

public record Http11Response(int status, String contentType, int contentLength, byte[] content) {

    private static final String DELIMITER = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_LENGTH = "Content-Length: ";

    public String getResponseHeader() {
        String responseHeader = String.join(
                DELIMITER,
                HTTP_VERSION + status + " " + HttpStatusCode.getStatusCode(status) + " ",
                CONTENT_TYPE + contentType,
                CONTENT_LENGTH + contentLength + " ") + DELIMITER + DELIMITER;

        return responseHeader;
    }

    public byte[] getResponseBody() {
        return content;
    }
}
