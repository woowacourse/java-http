package nextstep.jwp.model.web.response;

import nextstep.jwp.model.web.Headers;

public class HttpResponse {

    private StatusLine statusLine;
    private Headers headers;
    private ResponseBody body;

    public HttpResponse(StatusLine statusLine, Headers headers, ResponseBody body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }
}
