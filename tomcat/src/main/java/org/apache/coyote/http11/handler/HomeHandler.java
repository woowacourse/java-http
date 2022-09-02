package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

public class HomeHandler implements Handler {

    public static final String RESPONSE_BODY = "Hello world!";

    public HomeHandler(final HttpRequest httpRequest) {
    }

    @Override
    public String getResponse() {
        HttpResponse httpResponse = HttpResponse.of(ContentType.HTML, RESPONSE_BODY);
        return httpResponse.getResponse();
    }
}
