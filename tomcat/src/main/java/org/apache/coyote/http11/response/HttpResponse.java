package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.HttpCookie;
import org.apache.coyote.http11.header.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
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
        return new HttpResponse(
                new ResponseGeneral(HttpVersion.HTTP11, HttpStatus.OK),
                ResponseHeaders.empty(),
                ResponseBody.empty()
        );
    }

    public HttpResponse postProcess(HttpRequest request) {
        PostProcessMeta meta = new PostProcessMeta(request, this.body);
        return new HttpResponse(general, this.headers.replace(new HttpCookie()).postProcess(meta), body);
    }

    public HttpResponse update(HttpStatus status, String bodyString) {
        return new HttpResponse(
                new ResponseGeneral(HttpVersion.HTTP11, status),
                this.headers.update(bodyString),
                new ResponseBody(bodyString)
        );
    }

    public HttpResponse addHeader(ResponseHeader header) {
        return new HttpResponse(general, this.headers.replace(header), body);
    }

    @Override
    public String getAsString() {
        return general.getAsString() + "\n" + headers.getAsString() + "\n\n" + body.getAsString();
    }
}
