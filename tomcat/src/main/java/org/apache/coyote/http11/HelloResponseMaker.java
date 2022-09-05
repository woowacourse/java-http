package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HelloResponseMaker implements ResponseMaker {

    @Override
    public String createResponse(final HttpRequest httpRequest) {
        final String requestUrl = httpRequest.getRequestUrl();
        final var responseBody = "Hello world!";
        final HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, responseBody, ContentType.HTML);
        return httpResponse.toString();
    }
}
