package org.apache.coyote.http11.response;

import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ResponseHeaders;
import org.apache.coyote.http11.response.startLine.StatusLine;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(StatusLine statusLine, ResponseHeaders responseHeaders, ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }
}
