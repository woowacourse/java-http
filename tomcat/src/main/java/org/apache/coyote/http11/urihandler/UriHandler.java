package org.apache.coyote.http11.urihandler;

import java.io.IOException;
import org.apache.coyote.http11.UriResponse;
import org.apache.coyote.http11.request.HttpRequest;

public interface UriHandler {

    boolean canHandle(HttpRequest httpRequest);

    UriResponse getResponse(HttpRequest httpRequest) throws IOException;
}
