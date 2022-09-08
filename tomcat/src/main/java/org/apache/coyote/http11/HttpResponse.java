package org.apache.coyote.http11;

public class HttpResponse {

    private HttpHeader httpHeader;
    private HttpBody httpBody;

    public HttpResponse(final HttpHeader httpHeader, final HttpBody httpBody) {
        this.httpHeader = httpHeader;
        this.httpBody = httpBody;
    }

    public HttpResponse() {
    }

    public HttpResponse header(final HttpHeader httpHeader) {
        this.httpHeader = httpHeader;
        return this;
    }

    public HttpResponse body(final HttpBody httpBody) {
        this.httpBody = httpBody;
        return this;
    }

    public String getResponse() {
        return String.join("\r\n", httpHeader.getHeaderByFormat(),
                httpBody.getBodyByFormat());
    }
}
