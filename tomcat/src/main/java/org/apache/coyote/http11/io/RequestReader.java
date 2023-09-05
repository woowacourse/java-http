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

        return Request.from(
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
            final String[] header = line.split(": ");
            final String key = header[0];
            final String value = header[1].trim();
            headers.put(key, value);
        }

        return new Headers(headers);
    }

    private String readBody(final Headers headers) throws IOException {
        String body = "";
        if (headers.hasContentLength()) {
            final int contentLength = Integer.parseInt(headers.find("Content-Length"));
            final char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }
        return body;
    }
}
