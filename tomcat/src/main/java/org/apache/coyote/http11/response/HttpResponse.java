package org.apache.coyote.http11.response;

import org.apache.coyote.http11.message.HttpVersion;
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

    public static HttpResponse initial() {
        ResponseGeneral general = new ResponseGeneral(HttpVersion.HTTP11, HttpStatus.OK);
        ResponseHeaders headers = ResponseHeaders.empty();
        ResponseBody body = ResponseBody.empty();
        return new HttpResponse(general, headers, body);
    }

    public HttpResponse update(HttpStatus status, String bodyString) {
        return new HttpResponse(
                new ResponseGeneral(HttpVersion.HTTP11, status),
                this.headers.update(bodyString),
                new ResponseBody(bodyString)
        );
    }

    public HttpResponse updateBody(String bodyString) {
        return new HttpResponse(general, headers.update(bodyString), new ResponseBody(bodyString));
    }

    public HttpResponse addHeader(ResponseHeader header) {
        return new HttpResponse(general, this.headers.append(header), body);
    }

    @Override
    public String getAsString() {
        return general.getAsString() + "\n" + headers.getAsString() + "\n\n" + body.getAsString();
    }
}
