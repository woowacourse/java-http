package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.common.HttpBody;
import org.apache.coyote.http11.message.common.HttpHeaders;

public class HttpResponse {

    private final HttpStatusLine statusLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpResponse(HttpStatusLine statusLine, HttpHeaders headers, HttpBody body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse() {
        this(new HttpStatusLine(HttpStatus.OK), new HttpHeaders(), new HttpBody(""));
    }

    public void setStatusLine(HttpStatus httpStatus) {
        this.statusLine.setHttpStatus(httpStatus);
    }

    public void setHeader(String key, String value) {
        this.headers.setHeaders(key, value);
    }

    public void setBody(String body) {
        this.body.setBody(body);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(statusLine).append(System.lineSeparator());
        sb.append(headers).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(body);
        return sb.toString();
    }
}
