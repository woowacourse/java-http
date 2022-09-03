package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.message.HttpVersion;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class HttpResponse implements Response {

    private final ResponseGeneral general;
    private final ResponseHeaders headers;
    private final ResponseBody body;

    private HttpResponse(ResponseGeneral general, ResponseHeaders headers, ResponseBody body) {
        this.general = general;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse from(HttpStatus status, String bodyString) {
        ResponseGeneral general = new ResponseGeneral(HttpVersion.HTTP11, status);
        ResponseHeaders headers = ResponseHeaders.from(bodyString);
        ResponseBody body = new ResponseBody(bodyString);

        return new HttpResponse(general, headers, body);
    }

    public HttpResponse addHeader(ResponseHeader header) {
        this.headers.append(header);
        return this;
    }

    @Override
    public String getAsString() {
        return general.getAsString() + "\n" + headers.getAsString() + "\n\n" + body.getAsString();
    }
}
