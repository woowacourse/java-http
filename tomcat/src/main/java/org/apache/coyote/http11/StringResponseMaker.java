package org.apache.coyote.http11;

import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class StringResponseMaker implements ResponseMaker {

    @Override
    public String createResponse(final String requestUrl) {
        final var responseBody = "Hello world!";
        final HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, responseBody, ContentType.HTML);
        return httpResponse.toString();
    }
}
