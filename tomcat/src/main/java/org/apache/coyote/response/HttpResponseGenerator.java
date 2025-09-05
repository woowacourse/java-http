package org.apache.coyote.response;

public class HttpResponseGenerator {

    public static HttpResponse generate(final String body, final ContentType contentType, final HttpStatus httpStatus) {

        ResponseLine responseLine = new ResponseLine(httpStatus);
        ResponseHeader responseHeader = new ResponseHeader(body.getBytes().length, contentType);
        ResponseBody responseBody = new ResponseBody(body, contentType);

        return new HttpResponse(responseLine, responseHeader, responseBody);
    }
}
