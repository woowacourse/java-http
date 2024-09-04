package org.apache.coyote.http11;

public class Http11ResponseHeader {

    private static final String RESPONSE_HEADER_FORMAT = """
            %s
            Content-Type: %s;charset=utf-8
            Content-Length: %d
            """;

    private final StatusLine statusLine;
    private final ContentType contentType;
    private final int contentLength;

    public Http11ResponseHeader(StatusLine statusLine, ContentType contentType, int contentLength) {
        this.statusLine = statusLine;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public String getHeader() {
        return String.format(RESPONSE_HEADER_FORMAT, statusLine.toString(), contentType.getValue(), contentLength);
    }
}
