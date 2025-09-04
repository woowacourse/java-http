package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.HttpRequest;

public record HttpHandlerCondition(
        HttpMethod method,
        String uri
) {

    public static HttpHandlerCondition from(final HttpRequest request) {
        return new HttpHandlerCondition(
                request.method(),
                request.uri()
        );
    }
}
