package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface Handler {
    boolean canHandle(HttpRequest request);
    HttpResponse handle(HttpRequest request) throws IOException;
}
