package org.apache.coyote.http11.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.httprequest.RequestBody;
import org.apache.coyote.http11.httprequest.RequestHeaders;
import org.apache.coyote.http11.httprequest.RequestLine;

public abstract class HttpRequestParser {

    private static final String HEADER_SEPARATOR = ": ";
    private final BufferedReader reader;

    protected HttpRequestParser(final BufferedReader reader) {
        this.reader = reader;
    }

    public RequestLine parseRequestLine() throws IOException {
        final String rawRequestLine = reader.readLine();
        return RequestLine.from(rawRequestLine);
    }

    public RequestHeaders parseRequestHeaders() throws IOException {
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

    public abstract RequestBody parseRequestBody(final RequestHeaders requestHeaders) throws IOException;

    protected void readIntoBuffer(char[] buffer, int length) throws IOException {
        reader.read(buffer, 0, length);
    }
}
