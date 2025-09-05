package org.apache.coyote.response;

import org.apache.coyote.response.responseHeader.ResponseHeader;
import org.apache.coyote.response.responseLine.ResponseLine;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public HttpResponse(final ResponseLine responseLine, final ResponseHeader responseHeader, final ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public byte[] combine() {
        String response = String.join("\r\n",
                responseLine.toCombine() + " ",
                responseHeader.toCombine(),
                "",
                responseBody.getBody());

        return response.getBytes();
    }
}
