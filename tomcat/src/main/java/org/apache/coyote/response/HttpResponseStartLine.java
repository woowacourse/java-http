package org.apache.coyote.response;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.request.HttpRequest;

public class HttpResponseStartLine {

    private final HttpVersion httpVersion;
    private HttpStatus httpStatus;

    public HttpResponseStartLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static HttpResponseStartLine defaultStartLineFrom(HttpRequest httpRequest) {
        return new HttpResponseStartLine(httpRequest.getVersion(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void updateStatus(HttpStatus newHttpStatus) {
        this.httpStatus = newHttpStatus;
    }

    public boolean has5xxCode() {
        return httpStatus == HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String toString() {
        return String.join(
                " ",
                httpVersion.getValue(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.getText()
        );
    }
}
