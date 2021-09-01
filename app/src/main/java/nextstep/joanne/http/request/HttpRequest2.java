package nextstep.joanne.http.request;

public class HttpRequest2 {
    private final RequestLine requestLine;
    private final RequestHeaders2 requestHeaders;
    private final MessageBody messageBody;

    public HttpRequest2(RequestLine requestLine, RequestHeaders2 requestHeaders, MessageBody messageBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.messageBody = messageBody;
    }
}
