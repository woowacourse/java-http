package org.apache.coyote.http11.reqeust.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.reqeust.HttpRequest;

public class HttpRequestReader {

    private final InputStream inputStream;
    private final HttpRequestParser parser = HttpRequestParser.getInstance();

    public HttpRequestReader(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest read() throws IOException {
        final List<String> requestLines = getRequestLines();
        final String startLine = requestLines.getFirst();
        final List<String> headerLines = requestLines.subList(1, requestLines.size());

        return new HttpRequestBuilder()
                .method(parser.parseHttpMethod(startLine))
                .uri(parser.parseRequestUri(startLine))
                .queryParameters(parser.parseQueryParameters(startLine))
                .protocolVersion(parser.parseHttpVersion(startLine))
                .headers(parser.parseHeaders(headerLines))
                .body(getBodyIfExists(parser.parseHeaders(headerLines)))
                .build();
    }

    private List<String> getRequestLines() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> requestLines = new ArrayList<>();
        while (true) {
            final String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            requestLines.add(line);
        }

        return requestLines;
    }

    private String getBodyIfExists(final HttpHeaders headers) throws IOException {
        if (headers.getHeader("Content-Length") == null) {
            return null;
        }
        int contentLength = Integer.parseInt(headers.getHeader("Content-Length"));
        byte[] bodyBytes = inputStream.readNBytes(contentLength);

        return new String(bodyBytes, StandardCharsets.UTF_8);
    }
}
