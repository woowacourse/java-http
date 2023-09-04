package org.apache.coyote.http11;

public class HttpResponse {

    private static final String protocol = "HTTP/1.1";
    private HttpStatus httpStatus;
    private HttpHeaders headers;
    private String body;

    public HttpResponse() {
        this.headers = new HttpHeaders();
    }

    public void addHeader(String headerName, String value) {
        this.headers.put(headerName, value);
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }
}
