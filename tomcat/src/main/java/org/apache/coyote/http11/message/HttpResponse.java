package org.apache.coyote.http11.message;

public class HttpResponse {

    private StatusLine statusLine;
    private HttpResponseHeader httpResponseHeader;
    private String responseBody;

    public HttpResponse() {
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setHttpResponseHeader(HttpResponseHeader httpResponseHeader) {
        this.httpResponseHeader = httpResponseHeader;
    }

    public boolean hasResponseBody() {
        return this.responseBody != null;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpResponseHeader getHeader() {
        return this.httpResponseHeader;
    }
}
