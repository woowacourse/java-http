package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HttpRequestHandler {

    boolean supports(HttpRequest request);

    HttpResponse handle(HttpRequest request) throws IOException;
}
