package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String SPACE = " ";

    private HttpStatus status;
    private final ResponseHeader responseHeader;
    private final StringBuilder body;

    public HttpResponse(HttpStatus status, ResponseHeader responseHeader, StringBuilder body) {
        this.status = status;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public byte[] getBytes() {
        return String.join(CRLF,
                statusLine(),
                responseHeader.toResponseValue(),
                body).getBytes();
    }

    private String statusLine() {
        return HTTP_VERSION + SPACE + status.code() + SPACE + status + SPACE;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getHeader(String key) {
        return responseHeader.get(key);
    }
}
