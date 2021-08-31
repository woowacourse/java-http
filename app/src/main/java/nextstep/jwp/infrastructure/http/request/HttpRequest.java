package nextstep.jwp.infrastructure.http.request;

import nextstep.jwp.infrastructure.http.Headers;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final String messageBody;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final String messageBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
