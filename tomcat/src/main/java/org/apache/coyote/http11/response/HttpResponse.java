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

    public static HttpResponse status(HttpStatus status) {
        return new HttpResponse(status, ResponseHeader.empty(), new StringBuilder());
    }

    public static HttpResponse ok() {
        return new HttpResponse(HttpStatus.OK, ResponseHeader.empty(), new StringBuilder());
    }

    public HttpResponse body(String body) {
        this.body
                .append(body);
        return this;
    }

    public HttpResponse setHeader(String key, Object value) {
        responseHeader.put(key, value.toString());
        return this;
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

    public String getBody() {
        return body.toString();
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getHeader(String key) {
        return responseHeader.get(key);
    }

    public void redirect(String url) {
        status = HttpStatus.FOUND;
        responseHeader.put("Location", url);
    }
}
