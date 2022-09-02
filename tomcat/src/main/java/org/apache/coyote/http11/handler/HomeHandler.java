package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpRequest;

public class HomeHandler implements Handler {

    @Override
    public String handle(final HttpRequest httpRequest) {
        return createResponseMessage(ContentType.TEXT_HTML, "Hello world!");
    }
}
