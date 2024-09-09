package org.apache.coyote.mapping;

import org.apache.coyote.handler.Handler;
import org.apache.http.request.HttpRequest;

public abstract class HandlerMapping {
    abstract Handler getHandler(HttpRequest httpRequest);
}
