package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.HttpResponseLine;
import org.apache.coyote.http11.model.response.HttpStatusCode;

public class HomeHandler implements Handler {

    private static final String RESPONSE_BODY = "Hello world!";

    public HomeHandler(final HttpRequest httpRequest) {
    }

    @Override
    public String getResponse() {
        HttpResponseLine responseLine = HttpResponseLine.of(HttpStatusCode.OK);
        HttpResponse httpResponse = HttpResponse.of(responseLine, ContentType.HTML, RESPONSE_BODY);
        return httpResponse.getResponse();
    }
}
