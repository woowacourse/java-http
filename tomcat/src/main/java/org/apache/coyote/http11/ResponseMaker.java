package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;

public interface ResponseMaker {

    String createResponse(final String requestUrl) throws URISyntaxException, IOException;
}
