package org.apache.coyote.http11;

public class ResourceHandler implements Handler {

    @Override
    public String handle(final HttpRequest httpRequest) {
        String uri = httpRequest.getRequestTarget().getUri();
        String responseBody = FileReader.read(uri);
        return createResponseMessage(ContentType.from(uri), responseBody);
    }
}
