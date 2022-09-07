package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class HomeHandler implements Handler {

    private static final String RESPONSE_BODY = "Hello world!";
    private final HttpRequest httpRequest;

    public HomeHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        HttpResponse httpResponse = HttpResponse.of(ResponseStatusCode.OK, httpRequest.getVersion(), ContentType.HTML, RESPONSE_BODY);
        return httpResponse.getResponse();
    }
}
