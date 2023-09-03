package org.apache.coyote.http11.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final QueryParams queryParams;
    private final String body;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders headers,
        final QueryParams queryParams, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    private HttpRequest(final RequestLine requestLine, final QueryParams queryParams, final RequestHeaders headers) {
        this(requestLine, headers, queryParams, null);
    }

    public static HttpRequest from(final BufferedReader messageReader) throws IOException {
        final String startLine = messageReader.readLine();
        final RequestLine requestLine = RequestLine.from(startLine);
        final QueryParams queryParams = QueryParams.from(requestLine);
        final RequestHeaders requestHeaders = parseHeaders(messageReader);

        // TODO: 2023-09-03 header에 Content-Length 필드 있으면 body 읽어오고, 아니면 읽지 않도록
        return new HttpRequest(requestLine, queryParams, requestHeaders);
    }

    private static RequestHeaders parseHeaders(final BufferedReader messageReader) throws IOException {
        String readLine;
        final List<String> readHeaderLines = new ArrayList<>();
        while ((readLine = messageReader.readLine()) != null && !readLine.isEmpty()) {
            readHeaderLines.add(readLine);
        }

        return RequestHeaders.from(readHeaderLines);
    }

    public boolean isRequestOf(final HttpMethod method, final String path) {
        return requestLine.isMatchingRequest(method, path);
    }

    public Optional<String> findFirstHeaderValue(final String field) {
        return headers.findFirstValueOfField(field);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParamOf(final String field) {
        return queryParams.getValueOf(field);
    }
}
