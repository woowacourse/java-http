package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Body;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpVersion;

public class HttpResponse {

    private StatusLine statusLine;
    private Header header;
    private Body body;

    public HttpResponse() {
        header = new Header();
        body = Body.EMPTY;
    }

    public void setStatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        statusLine = new StatusLine(httpVersion, httpStatus);
    }

    public void setHeader(String headerName, String headerValue) {
        header.put(headerName, headerValue);
    }

    public void setBody(String body) {
        this.body = new Body(body);
    }

    public HttpVersion httpVersion() {
        return statusLine.httpVersion();
    }

    public HttpStatus httpStatus() {
        return statusLine.httpStatus();
    }

    public Header header() {
        return header;
    }

    public Body body() {
        return body;
    }
}
