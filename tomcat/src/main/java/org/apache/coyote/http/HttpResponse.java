package org.apache.coyote.http;



public class HttpResponse {

    private HttpStatus httpStatus;
    private Header header;
    private Body body;

    public HttpResponse(final HttpStatus httpStatus, final String url, final String bodyValue) {
        this.httpStatus = httpStatus;
        this.header = new Header(url);
        this.body = new Body(bodyValue);
    }

    public String getHttpStatus() {
        return httpStatus.getCode() + " " + httpStatus.getMessage() ;
    }

    public Header getHeader() {
        return header;
    }

    public String getBody() {
        return body.getValue();
    }
}
