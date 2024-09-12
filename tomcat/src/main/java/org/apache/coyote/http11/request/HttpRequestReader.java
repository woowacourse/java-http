package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestReader implements RequestReader {

    private final BufferedReader bufferedReader;

    public HttpRequestReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    @Override
    public String readRequestLine() throws IOException {
        return bufferedReader.readLine();
    }

    @Override
    public List<String> readRequestHeaders() throws IOException {
        List<String> headers = new ArrayList<>();

        String headerField = bufferedReader.readLine();

        while (!headerField.isBlank()) {
            headers.add(headerField);
            headerField = bufferedReader.readLine();
        }

        return headers;
    }

    @Override
    public String readRequestBody(int contentLength) throws IOException {
        StringBuilder builder = new StringBuilder();

        char[] bodyChars = new char[contentLength];
        bufferedReader.read(bodyChars, 0, contentLength);
        builder.append(bodyChars);

        return builder.toString();
    }
}
