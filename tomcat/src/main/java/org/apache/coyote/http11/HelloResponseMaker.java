package org.apache.coyote.http11;

public class HelloResponseMaker implements ResponseMaker{

    @Override
    public String createResponse(final String request) {
        final var responseBody = "Hello world!";
        final HttpResponse httpResponse = new HttpResponse(StatusCode.OK, ContentType.HTML, responseBody);
        return httpResponse.getResponse();
    }
}
