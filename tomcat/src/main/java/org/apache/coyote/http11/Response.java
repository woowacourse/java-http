package org.apache.coyote.http11;

import org.apache.coyote.http11.response.General;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeaders;

public class Response {

    private final General general;
    private final ResponseHeaders headers;
    private final ResponseBody responseBody;

    public Response(General general, ResponseHeaders headers, ResponseBody responseBody) {
        this.general = general;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static Response of(ResponseEntity responseEntity) {
        ResponseHeaders responseHeaders = ResponseHeaders.of(responseEntity);
        ResponseBody responseBody = ResponseBody.of(responseEntity, responseHeaders);
        return new Response(new General(responseEntity.getHttpStatus()), responseHeaders, responseBody);
    }

    public String asString() {
        return String.join("\r\n",
                this.general.asString(),
                this.headers.asString(),
                "",
                this.responseBody.value());
    }
}
