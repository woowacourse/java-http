package org.apache.coyote.http11.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final QueryParams queryParams;
    private final RequestBody body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers,
        final QueryParams queryParams, final RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader messageReader) throws IOException {
        final String startLine = messageReader.readLine();
        final RequestLine requestLine = RequestLine.from(startLine);
        final QueryParams queryParams = QueryParams.from(requestLine);
        final HttpHeaders httpHeaders = parseHeaders(messageReader);
        final RequestBody requestBody = readBody(messageReader, httpHeaders);

        return new HttpRequest(requestLine, httpHeaders, queryParams, requestBody);
    }

    private static HttpHeaders parseHeaders(final BufferedReader messageReader) throws IOException {
        String readLine;
        final List<String> readHeaderLines = new ArrayList<>();
        while ((readLine = messageReader.readLine()) != null && !readLine.isEmpty()) {
            readHeaderLines.add(readLine);
        }

        return HttpHeaders.from(readHeaderLines);
    }

    private static RequestBody readBody(final BufferedReader reader, final HttpHeaders httpHeaders) throws IOException {
        final Optional<String> contentLengthValue = httpHeaders.findFirstValueOfField("Content-Length");
        if (contentLengthValue.isEmpty()) {
            return RequestBody.empty();
        }

        final int contentLength = Integer.parseInt(contentLengthValue.get());

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

    public boolean isRequestOf(final HttpMethod method, final String path) {
        return requestLine.isMatchingRequest(method, path);
    }

    public Optional<String> findFirstHeaderValue(final String field) {
        return headers.findFirstValueOfField(field);
    }

    public boolean hasHeader(final String field) {
        return headers.getValuesOfField(field).isPresent();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParamOf(final String field) {
        return queryParams.getValueOf(field);
    }

    public String getBodyOf(final String key) {
        return body.getValueOf(key);
    }

    public Map<String, String> getRequestBody() {
        return body.getFieldsWithValue();
    }
}
