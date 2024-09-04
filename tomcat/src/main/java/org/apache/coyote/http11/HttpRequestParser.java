package org.apache.coyote.http11;

import org.apache.coyote.http11.header.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {
    public static HttpRequest parse(final InputStream is) throws IOException {
        final var inputReader = new BufferedReader(new InputStreamReader(is));
        final RequestLine requestLine = createRequestLine(inputReader);
        final Headers headers = createHeaders(inputReader);
        return new HttpRequest(requestLine, headers);
    }

    private static RequestLine createRequestLine(final BufferedReader reader) throws IOException {
        return RequestLine.create(reader.readLine());
    }

    private static Headers createHeaders(final BufferedReader reader) throws IOException {
        final Headers headers = new Headers();
        while (reader.ready()) {
            final String s = reader.readLine();
            if (s.isBlank()) {
                continue;
            }
            headers.put(s);
        }
        return headers;
    }
    private HttpRequestParser() {}
}
