package org.apache.coyote.handler;

import org.apache.http.request.HttpRequest;

public abstract class Handler {
    // TODO: 반환타입 HttpResponse로 변경
    abstract String handle(HttpRequest httpRequest);
}
