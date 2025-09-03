package org.apache.coyote.response;

public class HttpResponseGenerator {

    public static HttpResponse generate(final String body, final ContentType contentType, final HttpStatus httpStatus) {

        ResponseInfo responseInfo = new ResponseInfo(httpStatus);
        ResponseHeader responseHeader = new ResponseHeader(body.getBytes().length, contentType);
        ResponseBody responseBody = new ResponseBody(body, contentType);

        return new HttpResponse(responseInfo, responseHeader, responseBody);
    }
}
