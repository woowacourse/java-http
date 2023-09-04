package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.headers.RequestHeaders;
import org.apache.coyote.http11.request.requestLine.HttpMethod;
import org.apache.coyote.http11.request.requestLine.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpRequest {

    private static final String NEW_LINE = "\r\n";
    private static final String COOKIE = "Cookie";

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
        while (nextRequestHeaderLine != null && !nextRequestHeaderLine.equals("")) {
            requestHeaders.add(nextRequestHeaderLine);
            nextRequestHeaderLine = bufferedReader.readLine();
        }

        return String.join(NEW_LINE, requestHeaders);
    }

    private static String readRequestBody(final BufferedReader bufferedReader, final RequestHeaders requestHeaders) throws IOException {
        final int contentLength = requestHeaders.getContentLength();
        char[] buffer = new char[contentLength];
        final int bytesRead = bufferedReader.read(buffer, 0, contentLength);
        if (bytesRead == -1) {
            return "";
        }

        return new String(buffer);
    }

    public boolean isRequestOf(final HttpMethod httpMethod) {
        return requestLine.getHttpMethod() == httpMethod;
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

    public boolean hasSessionId() {
        return requestHeaders.getRequestHeader().containsKey(COOKIE);
    }

    public String findSessionIdFromRequestHeaders(final String sessionKey) {
        final String cookieValue = requestHeaders.search(COOKIE);
        final HttpCookie cookie = HttpCookie.from(cookieValue);

        return cookie.search(sessionKey);
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
