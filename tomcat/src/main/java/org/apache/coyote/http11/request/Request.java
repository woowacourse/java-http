package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class Request {
    private final RequestLine line;
    private final RequestHeader header;

    private Request(final RequestLine line, final RequestHeader header) {
        this.line = line;
        this.header = header;
    }

    public static Request convert(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.convert(bufferedReader.readLine());
        RequestHeader requestHeader = RequestHeader.convert(bufferedReader);
        return new Request(requestLine, requestHeader);
    }

    public RequestLine getLine() {
        return line;
    }

    public Map<String, String> getHeader() {
        return header.getHeader();
    }
}
