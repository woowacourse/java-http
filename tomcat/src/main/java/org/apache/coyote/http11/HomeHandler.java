package org.apache.coyote.http11;

public class HomeHandler implements Handler {

    @Override
    public String handle(final HttpRequest httpRequest) {
        return createResponseMessage(ContentType.TEXT_HTML, "Hello world!");
    }
}
