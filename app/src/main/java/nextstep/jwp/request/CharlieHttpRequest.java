package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;

public class CharlieHttpRequest implements HttpRequest {
    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_FORM_DATA = "application/x-www-form-urlencoded";

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
        if (isFormData(requestHeader)) {
            requestBody = RequestBody.of(bufferedReader, requestHeader.getHeader(CONTENT_LENGTH_HEADER_NAME));
        }
        return new CharlieHttpRequest(requestLine, requestHeader, requestBody);
    }

    private static boolean isFormData(RequestHeader requestHeader) {
        return CONTENT_TYPE_FORM_DATA.equalsIgnoreCase(requestHeader.getHeader(CONTENT_TYPE_HEADER_NAME));
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
