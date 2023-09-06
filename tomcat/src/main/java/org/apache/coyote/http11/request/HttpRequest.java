package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String LINE_SEPARATOR = ": ";

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(
            final RequestLine requestLine,
            final RequestHeader requestHeader,
            final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final RequestLine requestLine = RequestLine.from(firstLine);

        Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            final String[] split = line.split(LINE_SEPARATOR);
            headers.put(split[0], split[1]);
            line = bufferedReader.readLine();
        }
        final RequestHeader requestHeader = new RequestHeader(headers);
        if (!bufferedReader.ready()) {
            return new HttpRequest(requestLine, requestHeader, RequestBody.empty());
        }

        final int contentLength = Integer.parseInt(requestHeader.geHeaderValue("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        final RequestBody requestBody = RequestBody.parse(new String(buffer));
        return new HttpRequest(requestLine, requestHeader, requestBody);
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

    public RequestHeader getRequestHeaders() {
        return requestHeader;
    }

    public Map<String, String> getQueryParameter() {
        return requestLine.getRequestPath().getQueryParameter();
    }

    public Map<String, String> getRequestBody() {
        return requestBody.getBody();
    }
}
