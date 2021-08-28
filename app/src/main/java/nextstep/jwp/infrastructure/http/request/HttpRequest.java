package nextstep.jwp.infrastructure.http.request;

import nextstep.jwp.infrastructure.http.HttpHeaders;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final String messageBody;

    public HttpRequest(final HttpRequestLine requestLine, final HttpHeaders headers, final String messageBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
