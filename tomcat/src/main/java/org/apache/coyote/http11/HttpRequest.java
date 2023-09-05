package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final RequestLine requestLine = RequestLine.from(firstLine);

        Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            final String[] split = line.split(": ");
            headers.put(split[0], split[1]);
            line = bufferedReader.readLine();
        }
        final RequestHeaders requestHeaders = new RequestHeaders(headers);
        if (!bufferedReader.ready()) {
            return new HttpRequest(requestLine, requestHeaders, RequestBody.empty());
        }

        final RequestBody requestBody = RequestBody.parse(bufferedReader.readLine());
        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public RequestPath getRequestPath() {
        return requestLine.getRequestPath();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders.getHeaders();
    }

    public Map<String, String> getQueryParameter() {
        return requestLine.getRequestPath().getQueryParameter();
    }

    public Map<String, String> getRequestBody() {
        return requestBody.getBody();
    }
}
