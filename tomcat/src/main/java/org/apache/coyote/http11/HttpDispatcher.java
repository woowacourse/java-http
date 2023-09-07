package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;

public interface HttpDispatcher {
    HttpResponse handle(final HttpRequest request) throws IOException;
}
