package org.apache.coyote.http11.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.header.EntityHeaders;
import org.apache.coyote.http11.request.Request;

public class RequestReader {

    private final BufferedReader reader;

    public RequestReader(final InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public Request read() throws IOException {
        final var requestLine = reader.readLine();
        final var words = requestLine.split(" ");
        final var method = words[0];
        final var uri = words[1];
        final var protocol = words[2];

        final var allHeaders = readHeaders();
        final var body = readBody(new EntityHeaders(allHeaders));

        return Request.of(method, uri, protocol, allHeaders, body);
    }

    private Map<String, String> readHeaders() throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while (!"".equals((line = reader.readLine()))) {
            final var headerPair = line.split(": ");
            final var key = headerPair[0];
            final var value = headerPair[1].trim();

            headers.put(key, value);
        }

        return headers;
    }

    private String readBody(final EntityHeaders headers) throws IOException {
        if (headers.hasContentLength()) {
            final var contentLength = Integer.parseInt(headers.getContentLength());
            final var buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);

            return new String(buffer);
        }

        return "";
    }
}
