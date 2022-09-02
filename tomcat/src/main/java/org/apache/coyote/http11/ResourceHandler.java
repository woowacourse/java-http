package org.apache.coyote.http11;

import java.io.IOException;

public class ResourceHandler implements Handler {

    @Override
    public String handle(final HttpRequest httpRequest) throws IOException {
        String uri = httpRequest.getRequestTarget().getUri();
        String responseBody = createResponseBody(uri);
        return createResponseMessage(ContentType.from(uri), responseBody);
    }
}
