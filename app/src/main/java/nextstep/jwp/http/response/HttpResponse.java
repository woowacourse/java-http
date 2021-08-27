package nextstep.jwp.http.response;

import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.response.headers.ResponseHeaders;
import nextstep.jwp.http.response.statusline.StatusLine;
import nextstep.jwp.model.StaticResource;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final Body body;
    public HttpResponse(StatusLine statusLine, ResponseHeaders responseHeaders, Body body) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public static HttpResponse of(HttpStatus httpStatus, StaticResource staticResource) {
        StatusLine statusLine = StatusLine.from(httpStatus);
        ResponseHeaders responseHeaders = ResponseHeaders.from(staticResource);
        Body body = new Body(staticResource.getContent());

        return new HttpResponse(statusLine, responseHeaders, body);
    }
}
