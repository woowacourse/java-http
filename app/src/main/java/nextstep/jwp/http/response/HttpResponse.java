package nextstep.jwp.http.response;

import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.response.headers.ResponseHeaders;
import nextstep.jwp.http.response.statusline.StatusLine;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final Body body;

    public HttpResponse(StatusLine statusLine, ResponseHeaders responseHeaders, Body body) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }
}
