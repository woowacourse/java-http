package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class MappingLine { // GET /endPoint HTTP/1.1

    private final String requestMapping; // GET //-
    private final String url; // /endPoint
    private final String protocol; // HTTP/1.1

    private MappingLine(String requestMapping, String url, String protocol) {
        this.requestMapping = requestMapping;
        this.url = url;
        this.protocol = protocol;
    }

    public MappingLine(String[] parts) {
        this(parts[0], parts[1], parts[2]);
    }

    public MappingLine(BufferedReader bufferedReader) throws IOException {
        this(parseRequestLine(bufferedReader));
    }

    private static String[] parseRequestLine(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("request is Empty");
        }

        String[] parts = requestLine.split(" ", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException();
        }
        return parts;
    }

    public String getRequestMapping() {
        return requestMapping;
    }

    public String getUrl() {
        return url;
    }
}
