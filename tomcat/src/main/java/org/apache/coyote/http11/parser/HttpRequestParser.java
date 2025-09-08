package org.apache.coyote.http11.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.RequestBody;
import org.apache.coyote.http11.httprequest.RequestHeaders;
import org.apache.coyote.http11.httprequest.RequestLine;

public class HttpRequestParser {

    private static final String HEADER_SEPARATOR = ": ";
    private final BufferedReader reader;

    public HttpRequestParser(final BufferedReader reader) {
        this.reader = reader;
    }

    public HttpRequest readHttpRequest() throws IOException {
        final RequestLine requestLine = parseRequestLine();
        final RequestHeaders requestHeaders = parseRequestHeaders();
        final RequestBody requestBody = parseRequestBody(requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private RequestLine parseRequestLine() throws IOException {
        final String rawRequestLine = reader.readLine();
        return RequestLine.from(rawRequestLine);
    }

    private RequestHeaders parseRequestHeaders() throws IOException {
        String line;
        final List<String> rawHeaders = new ArrayList<>();
        while (!(line = reader.readLine()).isEmpty()) {
            final String[] header = line.split(HEADER_SEPARATOR);
            if (header.length != 2) {
                break;
            }
            rawHeaders.add(line);
        }
        return RequestHeaders.from(rawHeaders);
    }

    private RequestBody parseRequestBody(final RequestHeaders requestHeaders) throws IOException {
        final int contentLength = Integer.parseInt(requestHeaders.getOrDefault("Content-Length", "0"));
        if (contentLength == 0) {
            return RequestBody.createEmptyBody();
        }
        final char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        final String rawHttpRequestBody = new String(buffer);
        return RequestBody.from(rawHttpRequestBody);
    }
}
