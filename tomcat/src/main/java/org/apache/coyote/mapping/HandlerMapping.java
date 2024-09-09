package org.apache.coyote.mapping;

import org.apache.http.request.HttpRequest;

public abstract class HandlerMapping {
    abstract String mapping(HttpRequest httpRequest);
}
