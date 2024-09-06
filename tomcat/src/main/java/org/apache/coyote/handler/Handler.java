package org.apache.coyote.handler;

import org.apache.coyote.request.HttpRequest;

public abstract class Handler {
    abstract String handle(HttpRequest httpRequest);
}
