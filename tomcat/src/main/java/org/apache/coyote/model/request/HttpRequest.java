package org.apache.coyote.model.request;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine, final RequestHeader requestHeader, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final BufferedReader reader) {
        try {
            final RequestLine requestLine = RequestLine.of(reader.readLine());
            final RequestHeader requestHeader = createHeader(reader);
            final RequestBody requestBody = createBody(reader, requestHeader.getContentLength());
            return new HttpRequest(requestLine, requestHeader, requestBody);
        } catch (Exception e) {
            throw new IllegalArgumentException("Request 생성 오류");
        }
    }

    private static RequestHeader createHeader(final BufferedReader reader) {
        return RequestHeader.of(
                reader.lines()
                        .takeWhile(readLine -> !"".equals(readLine))
                        .collect(Collectors.toUnmodifiableList())
        );
    }

    private static RequestBody createBody(final BufferedReader reader, final int contentLength) {
        try {
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return RequestBody.of(new String(buffer));
        } catch (Exception e) {
            throw new IllegalArgumentException("Request Body 생성 오류");
        }
    }

    public boolean checkMethod(Method method) {
        return requestLine.checkMethod(method);
    }

    public Map<String, String> getParams() {
        return requestLine.getQueryParams();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Method getHttpMethod() {
        return requestLine.getMethod();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
