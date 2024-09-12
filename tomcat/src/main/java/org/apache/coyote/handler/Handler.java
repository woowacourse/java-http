package org.apache.coyote.handler;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public abstract class Handler {
    public abstract HttpResponse handle(HttpRequest httpRequest);
}
