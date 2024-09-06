package org.apache.coyote.http11.response;

public class ResponseContent {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String DEFAULT_CHARSET = "charset=utf-8";
    private static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    private static final String HEADER_CONTENT_LENGTH = "Content-Length: ";
    private static final String HEADER_SET_COOKIE = "Set-Cookie: ";

    private final HttpStatus httpStatus;
    private final String contentType;
    private final int contentLength;
    private final String body;
    private String cookie = "";

    public ResponseContent(HttpStatus httpStatus, String contentType, String body) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.contentLength = body.getBytes().length;
        this.body = body;
    }

    public ResponseContent(HttpStatus httpStatus, String contentType, String body, String cookie) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.contentLength = body.getBytes().length;
        this.body = body;
        this.cookie = cookie;
    }

    public String responseToString() {
        if (cookie.isEmpty()) {
            return String.join("\r\n",
                    HTTP_VERSION + " " + httpStatus.getValue() + " " + httpStatus.getReasonPhrase() + " ",
                    HEADER_CONTENT_TYPE + contentType + ";" + DEFAULT_CHARSET + " ",
                    HEADER_CONTENT_LENGTH + contentLength + " ",
                    "",
                    body);
        }
        return String.join("\r\n",
                HTTP_VERSION + " " + httpStatus.getValue() + " " + httpStatus.getReasonPhrase() + " ",
                HEADER_CONTENT_TYPE + contentType + ";" + DEFAULT_CHARSET + " ",
                HEADER_CONTENT_LENGTH + contentLength + " ",
                HEADER_SET_COOKIE + cookie,
                "",
                body);

    }
}
