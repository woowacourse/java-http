package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatusCode;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private StatusLine statusLine;
    private ResponseHeader header;
    private ResponseBody body;

    public void setResponse(HttpStatusCode statusCode, ResponseHeader header, ResponseBody body) {
        this.statusLine = new StatusLine(HTTP_VERSION, statusCode.getStatusCode(), statusCode.getMessage());
        this.header = header;
        this.body = body;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeader getHeader() {
        return header;
    }

    public ResponseBody getBody() {
        return body;
    }
}
