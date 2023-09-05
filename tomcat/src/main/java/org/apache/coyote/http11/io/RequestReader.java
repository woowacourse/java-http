package org.apache.coyote.http11.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.Headers;
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

        final var headers = readHeaders();
        final var body = readBody(headers);

        return Request.of(
                method,
                uri,
                headers,
                body
        );
    }

    private Headers readHeaders() throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while (!"".equals((line = reader.readLine()))) {
            final var header = line.split(": ");
            final var value = header[1].trim();
            final var key = header[0];
            headers.put(key, value);
        }

        return new Headers(headers);
    }

    private String readBody(final Headers headers) throws IOException {
        if (headers.hasContentLength()) {
            final var contentLength = Integer.parseInt(headers.find("Content-Length"));
            final var buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);

            return new String(buffer);
        }

        return "";
    }
}
