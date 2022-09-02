package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.message.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse implements Response {

    private final ResponseGeneral general;
    private final ResponseHeaders headers;
    private final ResponseBody body;

    private HttpResponse(ResponseGeneral general, ResponseHeaders headers, ResponseBody body) {
        this.general = general;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse from(ResponseEntity responseEntity) {
        ResponseGeneral general = new ResponseGeneral(HttpVersion.HTTP11, responseEntity.getStatus());
        ResponseHeaders headers = ResponseHeaders.from(responseEntity);
        ResponseBody body = new ResponseBody(responseEntity.getBody());

        return new HttpResponse(general, headers, body);
    }


    @Override
    public String getAsString() {
        return general.getAsString() + "\n" + headers.getAsString() + "\n\n" + body.getAsString();
    }
}
