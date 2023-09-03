package org.apache.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.common.HttpBody;
import org.apache.common.HttpHeaders;
import org.apache.common.HttpLine;

public class HttpRequest {

    private final HttpLine httpLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    private HttpRequest(HttpLine httpLine, HttpHeaders httpHeaders, HttpBody body) {
        this.httpLine = httpLine;
        this.httpHeaders = httpHeaders;
        this.httpBody = body;
    }

    public static HttpRequest of(BufferedReader bufferedReader) throws IOException {
        String firstLine = extractFirstLine(bufferedReader);

        List<String> headerLines = extractHeaderLines(bufferedReader);

//        String body = extractBody(bufferedReader);
        return new HttpRequest(HttpLine.of(firstLine), HttpHeaders.of(headerLines), new HttpBody(""));
    }

    private static String extractFirstLine(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
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

    private static String extractBody(BufferedReader bufferedReader) throws IOException {
        StringBuilder actual = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            actual.append(line);
            actual.append("\r\n");
        }

        return actual.toString();
    }

    public URI getUri() {
        return httpLine.convertPathToUri();
    }

    public String getTarget() {
        return httpLine.getRequestTarget();
    }
}
