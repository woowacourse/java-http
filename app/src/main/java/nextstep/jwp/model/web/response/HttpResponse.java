package nextstep.jwp.model.web.response;

import nextstep.jwp.model.web.Headers;

public class HttpResponse {

    private StatusLine statusLine;
    private Headers headers;
    private CustomHttpResponse body;

    public HttpResponse(StatusLine statusLine, Headers headers, CustomHttpResponse body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }
}
