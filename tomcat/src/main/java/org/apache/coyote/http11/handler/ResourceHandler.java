package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.util.FileReader;

public class ResourceHandler implements Handler {

    @Override
    public String handle(final HttpRequest httpRequest) {
        String uri = httpRequest.getRequestTarget().getUri();
        String responseBody = FileReader.read(uri);
        return createResponseMessage(ContentType.from(uri), responseBody);
    }
}
