package org.apache.coyote.httpRequest;

import org.apache.coyote.httpRequest.httpBody.HttpBody;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;

public class HttpRequest {

    final HttpHeader httpHeader;
    final HttpBody httpBody;

    public HttpRequest(
            final HttpHeader httpHeader,
            final HttpBody httpBody
    ) {
        this.httpHeader = httpHeader;
        this.httpBody = httpBody;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }
}
