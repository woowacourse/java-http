package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;

public class CharlieHttpRequest implements HttpRequest {
    private static final String BODY_LENGTH_HEADER_NAME = "Content-Length";

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public CharlieHttpRequest(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static CharlieHttpRequest of(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        RequestHeader requestHeader = RequestHeader.of(bufferedReader);
        RequestBody requestBody = RequestBody.empty();
        if (HttpMethod.POST.equals(requestLine.getHttpMethod())) {
            requestBody = RequestBody.of(bufferedReader, requestHeader.getHeader(BODY_LENGTH_HEADER_NAME));
        }
        return new CharlieHttpRequest(requestLine, requestHeader, requestBody);
    }

    @Override
    public String getResourceName() {
        return this.requestLine.getRequestPath();
    }

    @Override
    public String getAttribute(String name) {
        return this.requestBody.getValue(name);
    }

    @Override
    public RequestLine getRequestLine() {
        return requestLine;
    }

    @Override
    public RequestHeader getRequestHeader() {
        return requestHeader;
    }
}
