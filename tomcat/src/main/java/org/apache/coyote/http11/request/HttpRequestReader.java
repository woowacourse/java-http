package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestReader {

    private HttpRequestReader() {
    }

    public static HttpRequest read(final BufferedReader bufferedReader) throws IOException {
        final String startLine = bufferedReader.readLine();
        final Map<String, String> headers = extractHeaders(bufferedReader);
        final String body = extractBody(bufferedReader, headers.get("Content-Length"));
        return HttpRequest.of(startLine, headers, body);
    }

    private static Map<String, String> extractHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            if (line == null) {
                break;
            }
            final String[] split = line.split(": ");
            headers.put(split[0], split[1]);
        }
        return headers;
    }

    private static String extractBody(final BufferedReader bufferedReader, final String contentLength)
            throws IOException {
        int cbufSize = 0;
        if (contentLength != null) {
            cbufSize = Integer.parseInt(contentLength);
        }
        final char[] cbuf = new char[cbufSize];
        bufferedReader.read(cbuf);
        return new String(cbuf);
    }
}
