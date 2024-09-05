package com.techcourse.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest();

        parseStartLine(reader, request);
        parseHeaders(reader, request);
        parseBody(reader, request);

        return request;
    }

    private static void parseStartLine(BufferedReader reader, HttpRequest request) throws IOException {
        String startLine = reader.readLine();
        if (startLine != null) {
            String[] startLineParts = startLine.split(" ");
            request.setMethod(startLineParts[0]);
            request.setUri(startLineParts[1]);
        }
    }

    private static void parseHeaders(BufferedReader reader, HttpRequest request) throws IOException {
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ");
            request.getHeaders().put(headerParts[0], headerParts[1]);
        }
    }

    private static void parseBody(BufferedReader reader, HttpRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        while (reader.ready()) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            body.append(line).append("\n");
        }
        request.setBody(body.toString().trim());
    }
}
