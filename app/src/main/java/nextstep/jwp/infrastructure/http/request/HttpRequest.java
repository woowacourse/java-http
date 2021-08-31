package nextstep.jwp.infrastructure.http.request;

import nextstep.jwp.infrastructure.http.HttpHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String messageBody;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final String messageBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
