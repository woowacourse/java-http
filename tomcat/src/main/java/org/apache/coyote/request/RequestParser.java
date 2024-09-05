package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestParser {

    public Request parse(BufferedReader reader) throws IOException {
        RequestLine requestLine = getRequestLine(reader);
        return new Request(requestLine, null); // todo
    }

    private RequestLine getRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IllegalArgumentException("Request line is null");
        }
        return new RequestLine(requestLine);
    }
}
