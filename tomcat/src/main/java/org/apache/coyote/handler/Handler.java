package org.apache.coyote.handler;

import org.apache.http.request.HttpRequest;

public abstract class Handler {
    abstract String handle(HttpRequest httpRequest);
}
