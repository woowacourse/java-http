package org.apache.response;

public class HttpResponse {

    private static final String VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE_PREFIX = "Content-Type: ";
    private static final String CONTENT_LENGTH_PREFIX = "Content-Length: ";
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String ENTER = "\n";

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
        return String.join(ENTER,
                VERSION + responseStatus + SPACE,
                CONTENT_TYPE_PREFIX + contentType + SPACE,
                CONTENT_LENGTH_PREFIX + responseBody.getBytes().length + SPACE,
                EMPTY,
                responseBody
                );
    }

    private static String getContentType(final String url) {
        if (url.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}
