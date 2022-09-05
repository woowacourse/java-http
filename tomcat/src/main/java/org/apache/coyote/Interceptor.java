package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;

public interface Interceptor {
    void handle(HttpRequest httpRequest);
}
