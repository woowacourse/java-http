package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Http11RequestReader {

    public static Http11Request parse(final InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = extractRequestHeadersWithStartLine(reader);

        StartLine startLine = StartLine.extractStartLine(lines.getFirst());
        Http11RequestHeaders headers = Http11RequestHeaders.extractHeaders(lines);
        Http11RequestBody body = Http11RequestBody.from(reader, headers);

        return new Http11Request(startLine, headers, body);
    }

    private static List<String> extractRequestHeadersWithStartLine(final BufferedReader bufferedReader)
            throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
            requestHeaders.add(requestLine);
        }

        return requestHeaders;
    }
}
