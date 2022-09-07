package org.apache.coyote.http11.handler;

import org.apache.coyote.model.request.ContentType;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;

public class HomeHandler implements Handler {

    public static final String HELLO_WORLD = "Hello world!";
    private final HttpRequest httpRequest;

    public HomeHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        return HttpResponse.of(ContentType.HTML.toString(), HELLO_WORLD, ResponseLine.of(StatusCode.OK))
                .getResponse();
    }
}
