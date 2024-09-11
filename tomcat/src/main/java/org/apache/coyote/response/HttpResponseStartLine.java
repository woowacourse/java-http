package org.apache.coyote.response;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.request.HttpRequest;

public class HttpResponseStartLine {

    private HttpStatus httpStatus;
    private final HttpVersion httpVersion;

    public HttpResponseStartLine(HttpStatus httpStatus, HttpVersion httpVersion) {
        this.httpStatus = httpStatus;
        this.httpVersion = httpVersion;
    }

    public static HttpResponseStartLine defaultStartLineFrom(HttpRequest httpRequest) {
        return new HttpResponseStartLine(HttpStatus.INTERNAL_SERVER_ERROR, httpRequest.getVersion());
    }

    public void updateStatus(HttpStatus newHttpStatus) {
        this.httpStatus = newHttpStatus;
    }

    public boolean has5xxCode() {
        return httpStatus == HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return String.join(
                " ",
                httpVersion.getValue(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.getText() + " "
        );
    }
}
