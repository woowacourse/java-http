package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        RequestHeaders requestHeaders = RequestHeaders.from(extractHeaders(bufferedReader));
        RequestBody requestBody = RequestBody.from(extractBody(bufferedReader, requestHeaders));
        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private static List<String> extractHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headers = new ArrayList<>();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isBlank()) {
                break;
            }
            headers.add(line);
        }
        return headers;
    }

    private static char[] extractBody(BufferedReader bufferedReader, RequestHeaders requestHeaders) throws IOException {
        int contentLength = requestHeaders.getContentLength();
        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);
        return body;
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

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", requestHeaders=" + requestHeaders +
                ", requestBody=" + requestBody +
                '}';
    }
}
