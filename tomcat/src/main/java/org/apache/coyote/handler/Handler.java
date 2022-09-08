package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.HttpResponse;

public interface Handler {

    HttpResponse run(HttpRequest httpRequest) throws URISyntaxException, IOException;
}
