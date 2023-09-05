package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

public class HelloResponseMaker extends ResponseMaker {

    @Override
    public String createResponse(final HttpRequest request) {
        final var responseBody = "Hello world!";
        final HttpResponse httpResponse = new HttpResponse(StatusCode.OK, ContentType.HTML, responseBody);
        return httpResponse.getResponse();
    }
}
