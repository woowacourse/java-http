package org.apache.coyote.http11.httpmessage.response;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.httpmessage.HttpHeader;

public class HttpResponse {

    StatusCode statusCode;
    HttpHeader httpHeader;
    String body;

    public HttpResponse() {
        this.statusCode = null;
        this.httpHeader = new HttpHeader(new LinkedHashMap<>());
        this.body = null;
    }

    public HttpResponse(final StatusCode statusCode, final HttpHeader httpHeader,
        final String body) {
        this.statusCode = statusCode;
        this.httpHeader = httpHeader;
        this.body = body;
    }

    public void setStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
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

    public void addHeader(final String headerName, final String headerValue) {
        httpHeader.addHeader(headerName, headerValue);
    }

    public void setBody(final String body) {
        this.body = body;
    }
}
