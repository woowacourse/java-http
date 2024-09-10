package org.apache.coyote.http11.response;

import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.ResponseHeaders;
import org.apache.coyote.http11.response.startLine.HttpStatus;
import org.apache.coyote.http11.response.startLine.StatusLine;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(HttpStatus httpStatus, ContentType contentType, String body) {
        this.statusLine = new StatusLine(httpStatus);
        this.responseHeaders = ResponseHeaders.of(contentType, body);
        this.responseBody = new ResponseBody(body);
    }

    public String write() {
        return String.join("\r\n",
                statusLine.write(),
                responseHeaders.write(),
                responseBody.write());
    }
}
