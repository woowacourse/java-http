package org.apache.coyote.mapping;

import org.apache.coyote.request.HttpRequest;

public abstract class HandlerMapping {
    abstract String mapping(HttpRequest httpRequest);
}
