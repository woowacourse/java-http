package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeaders;
import org.apache.coyote.http11.request.startLine.HttpMethod;
import org.apache.coyote.http11.request.startLine.RequestLine;
import org.apache.coyote.http11.response.header.ContentType;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(HttpRequestReader httpRequestReader) throws IOException {
        RequestLine requestLine = new RequestLine(httpRequestReader.readRequestLine());
        RequestHeaders requestHeaders = new RequestHeaders(httpRequestReader.readRequestHeaders());

        int contentLength = requestHeaders.get("Content-Length")
                .map(Integer::parseInt)
                .orElse(0);

        if (contentLength > 0) {
            String mediaType = requestHeaders.get(ContentType.HEADER)
                    .orElseThrow(() -> new IllegalArgumentException("요청 헤더를 찾을 수 없습니다."));

            RequestBody requestBody = new RequestBody(mediaType, httpRequestReader.readRequestBody(contentLength));
            return new HttpRequest(requestLine, requestHeaders, requestBody);
        }

        return new HttpRequest(requestLine, requestHeaders, RequestBody.empty());
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return requestLine.isMethod(httpMethod);
    }

    public Optional<String> getHeader(String header) {
        return requestHeaders.get(header);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }
}
