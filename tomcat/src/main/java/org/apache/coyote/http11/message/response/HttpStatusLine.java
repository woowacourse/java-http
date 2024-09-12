package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpVersion;

public class HttpStatusLine {

    private final HttpVersion httpVersion;
    private final HttpMethod httpMethod;

    public HttpStatusLine(final HttpMethod httpMethod, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
    }
}
