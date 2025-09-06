package org.apache.coyote.response;

import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseHeader.ResponseHeader;
import org.apache.coyote.response.responseLine.HttpStatus;
import org.apache.coyote.response.responseLine.ResponseLine;

public class HttpResponse {

    private ResponseLine responseLine;
    private ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public HttpResponse(final ResponseLine responseLine, final ResponseHeader responseHeader,
                        final ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public HttpResponse() {
        this.responseLine = new ResponseLine();
        this.responseHeader = new ResponseHeader();
        this.responseBody = new ResponseBody();
    }

    public byte[] combine() {
        String response = String.join("\r\n",
                responseLine.toCombine() + " ",
                responseHeader.toCombine(),
                "",
                responseBody.getBody());

        return response.getBytes();
    }

    public void init(final String body, final ContentType contentType, final HttpStatus httpStatus) {
        this.responseLine = responseLine.init(httpStatus);
        this.responseHeader = responseHeader.init(body.getBytes().length, contentType);
        this.responseBody = responseBody.init(body);
    }
}
