package org.apache.coyote.http11;

public class HttpHeader {

    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String requestStartLine;
    private final String requestHeaders;

    public HttpHeader(final String requestStartLine, final String requestHeaders) {
        this.requestStartLine = requestStartLine;
        this.requestHeaders = requestHeaders;
    }

    public String getResponseHeader(final StatusCode statusCode, final String response) {
        return String.join("\r\n",
                HTTP_VERSION + statusCode.getStatusMessage(),
                "Content-Type: " + getResponseContentType().getMIMEType() +";charset=utf-8 ",
                "Content-Length: " + response.getBytes().length + " ",
                "");
    }

    private ContentType getResponseContentType() {
        final String path = requestStartLine.split(" ")[1];
        if (path.contains(EXTENSION_DELIMITER)) {
            final String[] splitExtension = path.split("\\" + EXTENSION_DELIMITER);
            return ContentType.matchMIMEType(splitExtension[splitExtension.length - 1]);
        }
        return ContentType.HTML;
    }

    public String getStartLine() {
        return requestStartLine;
    }
}
