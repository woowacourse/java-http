package org.apache.coyote.http11.reqeust.handler;

import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.HttpRequest;

public record HttpRequestHandlerCondition(
        HttpMethod method,
        String uri
) {

    public static HttpRequestHandlerCondition from(final HttpRequest request) {
        return new HttpRequestHandlerCondition(
                request.method(),
                request.uri()
        );
    }
}
