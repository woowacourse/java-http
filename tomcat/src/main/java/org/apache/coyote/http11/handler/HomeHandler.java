package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseLine;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class HomeHandler implements Handler {

    private static final String RESPONSE_BODY = "Hello world!";

    public HomeHandler(final HttpRequest httpRequest) {
    }

    @Override
    public String getResponse() {
        ResponseLine responseLine = ResponseLine.of(ResponseStatusCode.OK);
        HttpResponse httpResponse = HttpResponse.of(responseLine, ContentType.HTML, RESPONSE_BODY);
        return httpResponse.getResponse();
    }
}
