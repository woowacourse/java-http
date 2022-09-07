package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Http11Request {

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    private Http11Request(final RequestLine requestLine, final HttpHeaders httpHeaders, final String requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static Http11Request from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = generateRequestLine(bufferedReader);
        final HttpHeaders httpHeaders = generateHttpHeader(bufferedReader);
        final String requestBody = generateHttpRequestBody(httpHeaders, bufferedReader);

        return new Http11Request(requestLine, httpHeaders, requestBody);
    }

    private static RequestLine generateRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        return RequestLine.from(line);
    }

    private static HttpHeaders generateHttpHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> httpHeaders = new ArrayList<>();
        var line = bufferedReader.readLine();

        if (Objects.isNull(line)) {
            return HttpHeaders.from(httpHeaders);
        }

        while (!"".equals(line)) {
            httpHeaders.add(line);
            line = bufferedReader.readLine();
        }
        return HttpHeaders.from(httpHeaders);
    }

    public static String generateHttpRequestBody(final HttpHeaders httpHeaders, final BufferedReader bufferedReader)
            throws IOException {
        final var contentLength = httpHeaders.getContentLength();
        final var length = contentLength.getContentLength();
        final char[] body = new char[length];

        bufferedReader.read(body);

        return new String(body);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
