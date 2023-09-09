package org.apache.coyote.http11.httpmessage.response;

import org.apache.coyote.http11.httpmessage.HttpHeader;

public class HttpResponse {

    final StatusCode statusCode;
    final HttpHeader httpHeader;
    final String body;

    public HttpResponse(final StatusCode statusCode, final HttpHeader httpHeader,
        final String body) {
        this.statusCode = statusCode;
        this.httpHeader = httpHeader;
        this.body = body;
    }

    public String makeToString() {
        return String.join("\r\n",
            "HTTP/1.1 " + statusCode.getStatus() + " ",
            httpHeader.makeResponseForm(),
            "",
            body);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public String getBody() {
        return body;
    }
}
