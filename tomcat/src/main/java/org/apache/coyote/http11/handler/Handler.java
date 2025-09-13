package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface Handler {
    boolean canHandle(final HttpRequest request);
    void handle(final HttpRequest request, final HttpResponse response) throws IOException;
}
