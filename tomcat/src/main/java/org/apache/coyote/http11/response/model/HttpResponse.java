package org.apache.coyote.http11.response.model;

import org.apache.coyote.http11.domain.body.ResponseBody;
import org.apache.coyote.http11.domain.header.ResponseHeader;
import org.apache.coyote.http11.response.domain.ResponseLine;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final ResponseHeader header;
    private final ResponseBody responseBody;

    public HttpResponse(ResponseLine responseLine, ResponseHeader header, ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.header = header;
        this.responseBody = responseBody;
    }

    public byte[] combineResponseToBytes() {
        String response = String.join("\r\n",
                responseLine.toCombinedResponse(),
                header.toCombinedHeader(),
                "",
                responseBody.getBody());

        return response.getBytes();
    }
}
