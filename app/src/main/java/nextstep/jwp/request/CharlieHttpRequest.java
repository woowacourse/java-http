package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;

public class CharlieHttpRequest implements HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeader requestHeader;

    public CharlieHttpRequest(RequestLine requestLine, RequestHeader requestHeader) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
    }

    public static CharlieHttpRequest of(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        RequestHeader requestHeader = RequestHeader.of(bufferedReader);
        return new CharlieHttpRequest(requestLine, requestHeader);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }
}
