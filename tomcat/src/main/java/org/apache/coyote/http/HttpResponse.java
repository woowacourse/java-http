package org.apache.coyote.http;



public class HttpResponse {

    private Header header;
    private Body body;

    public HttpResponse(final HttpRequest httpRequest) {
        this.header = new Header(httpRequest.getUrl());
        this.body = new Body(httpRequest.getUrl());
    }

    public Header getHeader() {
        return header;
    }

    public String getBody() {
        return body.getValue();
    }
}
