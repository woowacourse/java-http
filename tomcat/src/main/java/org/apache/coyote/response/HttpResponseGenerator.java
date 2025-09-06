package org.apache.coyote.response;

import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseHeader.ResponseHeader;
import org.apache.coyote.response.responseLine.HttpStatus;
import org.apache.coyote.response.responseLine.ResponseLine;

public class HttpResponseGenerator {

    public static HttpResponse of(final String body, final ContentType contentType, final HttpStatus httpStatus) {

        ResponseLine responseLine = new ResponseLine(httpStatus);
        ResponseHeader responseHeader = new ResponseHeader(body.getBytes().length, contentType);
        ResponseBody responseBody = new ResponseBody(body);

        return new HttpResponse(responseLine, responseHeader, responseBody);
    }

    public static HttpResponse create() {
        return new HttpResponse();
    }
}
