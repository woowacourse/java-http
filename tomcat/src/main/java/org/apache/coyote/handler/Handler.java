package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.MyHttpResponse;

public interface Handler {

    MyHttpResponse run(HttpRequest httpRequest) throws URISyntaxException, IOException;
}
