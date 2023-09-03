package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.headers.RequestHeaders;
import org.apache.coyote.http11.request.requestLine.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpRequest {

    private static final String NEW_LINE = "\r\n";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final RequestHeaders requestHeaders = RequestHeaders.from(readRequestHeader(bufferedReader));
        final RequestBody requestBody = RequestBody.from(readRequestBody(bufferedReader, requestHeaders));

        return new HttpRequest(
                requestLine,
                requestHeaders,
                requestBody
        );
    }

    private static String readRequestHeader(final BufferedReader bufferedReader) throws IOException {
        return readNextChunk(bufferedReader);
    }

    private static String readNextChunk(final BufferedReader bufferedReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();

        String nextRequestHeaderLine = bufferedReader.readLine();
        while (nextRequestHeaderLine != null && !nextRequestHeaderLine.isBlank()) {
            requestHeaders.add(nextRequestHeaderLine);
            nextRequestHeaderLine = bufferedReader.readLine();
        }

        return String.join(NEW_LINE, requestHeaders);
    }

    private static String readRequestBody(final BufferedReader bufferedReader, final RequestHeaders requestHeaders) throws IOException {
        if (requestHeaders.isContentLengthNull()) {
            return "";
        }

        return readNextChunk(bufferedReader);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeader() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(requestHeaders, that.requestHeaders) && Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, requestHeaders, requestBody);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", requestHeader=" + requestHeaders +
                ", requestBody=" + requestBody +
                '}';
    }
}
