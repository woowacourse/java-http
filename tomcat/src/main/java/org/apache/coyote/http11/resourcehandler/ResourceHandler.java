package org.apache.coyote.http11.resourcehandler;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface ResourceHandler {

    HttpResponse handle(final HttpRequest httpRequest) throws IOException;
}
