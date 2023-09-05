package org.apache.coyote.http11.request;

import java.util.Objects;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeader requestHeader) {
        this(requestLine, requestHeader, null);
    }

    public HttpRequest(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(String requestLine, String requestHeader) {
        return new HttpRequest(
                RequestLine.from(requestLine),
                RequestHeader.from(requestHeader)
        );
    }

    public boolean hasBody() {
        return requestHeader.hasContent();
    }

    public int contentLength() {
        return requestHeader.contentLength();
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public RequestHeader requestHeader() {
        return requestHeader;
    }

    public RequestBody requestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = RequestBody.from(requestBody);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(requestHeader,
                that.requestHeader) && Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, requestHeader, requestBody);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", requestHeader=" + requestHeader +
                ", requestBody=" + requestBody +
                '}';
    }
}
