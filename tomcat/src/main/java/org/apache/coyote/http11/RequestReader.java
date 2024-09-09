package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RequestReader {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;

    public RequestReader(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLine = readRequestLine(reader);
        this.requestHeaders = readRequestHeaders(reader);
    }

    private RequestLine readRequestLine(BufferedReader reader) throws IOException {
        return new RequestLine(reader.readLine());
    }

    private RequestHeaders readRequestHeaders(BufferedReader reader) throws IOException {
        List<String> rawRequestHeaders = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            rawRequestHeaders.add(line);
        }
        return new RequestHeaders(rawRequestHeaders);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }
}
