package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpRequest;

import java.io.IOException;

public interface Controller {
    void service(final HttpRequest request,
                   final HttpResponse response) throws IOException;
}
