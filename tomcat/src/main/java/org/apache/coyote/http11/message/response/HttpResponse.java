package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.HttpVersion;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final Headers headers;
    private final ResponseBody body;

    public HttpResponse(HttpVersion httpVersion, HttpStatus httpStatus,
                         Headers headers, ResponseBody body) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public byte[] getBytes() {
        String statusLine = httpVersion + " " + httpStatus + " ";
        String message = String.join("\r\n", statusLine,
                headers.toString(), "", body.toString());
        return message.getBytes();
    }
}
