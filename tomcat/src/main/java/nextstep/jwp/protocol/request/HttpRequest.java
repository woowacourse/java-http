package nextstep.jwp.protocol.request;

import nextstep.jwp.protocol.request.headers.RequestHeaders;
import nextstep.jwp.protocol.request.line.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
    }

}
