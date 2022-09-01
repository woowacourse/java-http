package nextstep.jwp.http.reqeust;

import nextstep.jwp.http.HttpHeader;

public class HttpRequest {

    private HttpRequestLine httpRequestLine;
    private HttpHeader httpHeaders;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeader httpHeaders) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
    }

    public String getUrl() {
        return httpRequestLine.getUrl();
    }
}
