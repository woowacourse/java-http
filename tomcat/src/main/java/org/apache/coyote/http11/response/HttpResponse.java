package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_LENGTH = "Content-Length: ";
    private static final String BLANK = "";
    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";

    private final String responseStatus;
    private final String contentType;
    private final String responseBody;

    public HttpResponse(
            final String responseStatus,
            final String contentType,
            final String responseBody
    ) {
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return String.join(CRLF,
                HTTP_VERSION + responseStatus + SPACE,
                CONTENT_TYPE + contentType + SPACE,
                CONTENT_LENGTH + responseBody.getBytes().length + SPACE,
                BLANK,
                responseBody
                );
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}
