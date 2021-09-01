package nextstep.joanne.server.http.request;

import nextstep.joanne.server.http.Headers;
import nextstep.joanne.server.http.HttpMethod;
import nextstep.joanne.server.http.HttpSession;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Headers requestHeaders;
    private final MessageBody messageBody;

    public HttpRequest(RequestLine requestLine, Headers requestHeaders, MessageBody messageBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.messageBody = messageBody;
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public boolean isSameMethod(HttpMethod method) {
        return requestLine.isSameHttpMethod(method);
    }

    public Headers requestHeaders() {
        return requestHeaders;
    }

    public MessageBody messageBody() {
        return messageBody;
    }

    public String bodyOf(String key) {
        return messageBody.get(key);
    }

    public String uri() {
        return requestLine.uri();
    }

    public boolean hasCookie() {
        return requestHeaders.hasCookie();
    }

    public boolean hasSessionId() {
        return requestHeaders.hasSessionId();
    }

    public String getNewSessionId() {
        return requestHeaders.makeSessionId();
    }

    public HttpSession getSession() {
        return requestHeaders.getSession();
    }
}
