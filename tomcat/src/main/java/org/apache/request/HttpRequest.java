package org.apache.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class HttpRequest {

    private final HttpLine httpLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    private HttpRequest(HttpLine httpLine, HttpHeaders httpHeaders, HttpBody httpBody) {
        this.httpLine = httpLine;
        this.httpHeaders = httpHeaders;
        this.httpBody = httpBody;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        String firstLine = bufferedReader.readLine();

        List<String> headerLines = extractHeaderLines(bufferedReader);

        return new HttpRequest(HttpLine.from(firstLine), HttpHeaders.from(headerLines), new HttpBody(""));
    }

    private static List<String> extractHeaderLines(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if ("".equals(line)) {
                break;
            }
            lines.add(line);
        }

        return lines;
    }

    public URI getUri() {
        return httpLine.convertPathToUri();
    }

    public String getTarget() {
        return httpLine.getRequestTarget();
    }
}
