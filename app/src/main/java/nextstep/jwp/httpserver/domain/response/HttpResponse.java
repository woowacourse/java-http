package nextstep.jwp.httpserver.domain.response;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Headers;

public class HttpResponse {
    private StatusLine statusLine;
    private Headers headers;
    private Body body;

    public HttpResponse() {
    }

    public HttpResponse(StatusLine statusLine, Headers headers, Body body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}
