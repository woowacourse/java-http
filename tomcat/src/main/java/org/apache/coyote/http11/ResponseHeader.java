package org.apache.coyote.http11;

public class ResponseHeader {

    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String path;

    public ResponseHeader(final String path) {
        this.path = path;
    }

    public String getHeader(StatusCode statusCode, String response) {
        return String.join("\r\n",
                HTTP_VERSION + statusCode.getStatusMessage(),
                "Content-Type: " + getContentType().getMIMEType() +";charset=utf-8 ",
                "Content-Length: " + response.getBytes().length + " ",
                "");
    }

    private ContentType getContentType() {
        if (path.contains(EXTENSION_DELIMITER)) {
            final String[] splitExtension = path.split("\\" + EXTENSION_DELIMITER);
            return ContentType.matchMIMEType(splitExtension[splitExtension.length - 1]);
        }
        return ContentType.HTML;
    }
}
