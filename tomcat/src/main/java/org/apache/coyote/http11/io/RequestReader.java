package org.apache.coyote.http11.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.header.EntityHeaders;
import org.apache.coyote.http11.common.header.HeaderName;
import org.apache.coyote.http11.request.Request;

public class RequestReader {

    private final BufferedReader reader;

    public RequestReader(final InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public Request read() throws IOException {
        final var requestHead = reader.readLine();
        final var head = requestHead.split(" ");
        final var method = head[0];
        final var uri = head[1];

        final var allHeaders = readHeaders();
        final var body = readBody(new EntityHeaders(allHeaders));

        return Request.of(method, uri, allHeaders, body);
    }

    private Map<HeaderName, String> readHeaders() throws IOException {
        final Map<HeaderName, String> headers = new HashMap<>();
        String line;
        while (!"".equals((line = reader.readLine()))) {
            final var headerPair = line.split(": ");
            final var key = headerPair[0];
            final var value = headerPair[1].trim();

            headers.put(HeaderName.find(key), value);
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
