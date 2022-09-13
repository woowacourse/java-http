package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestReader {

    private RequestReader() {
    }

    public static HttpRequest readHttpRequest(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = RequestLine.parse(reader.readLine());
        final RequestHeader header = readHeaders(reader);
        final RequestBody body = readBody(reader);

        return new HttpRequest(requestLine, header, body);
    }

    private static RequestHeader readHeaders(final BufferedReader reader) throws IOException {
        final List<String> lines = new ArrayList<>();

        final String endOfHeader = "";
        while (true) {
            final String line = reader.readLine();
            if (endOfHeader.equals(line)) {
                break;
            }
            lines.add(line);
        }
        return RequestHeader.parse(lines);
    }

    private static RequestBody readBody(final BufferedReader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();
        while (reader.ready()) {
            builder.append((char) reader.read());
        }
        final String body = builder.toString();
        return RequestBody.parse(body);
    }
}
