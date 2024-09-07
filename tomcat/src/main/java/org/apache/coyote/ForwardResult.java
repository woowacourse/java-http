package org.apache.coyote;

import org.apache.coyote.http11.response.ResponseHeader;

public record ForwardResult(HttpStatusCode statusCode, String path, ResponseHeader header) {

    public ForwardResult(HttpStatusCode statusCode, String path) {
        this(statusCode, path, new ResponseHeader());
    }
}
