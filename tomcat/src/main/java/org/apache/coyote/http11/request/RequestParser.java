package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static RequestHeader getRequestHeader(final BufferedReader reader) throws IOException {
        final Map<String, String> header = new HashMap<>();
        String line;

        while (!(line = reader.readLine()).isBlank()) {
            final var parts = line.split(": ", 2);
            header.put(parts[0], parts[1]);
        }
        return new RequestHeader(header);
    }

    public static RequestBody getRequestBody(final BufferedReader reader, final int contentLength) throws IOException {
        final var buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return parseRequestBody(new String(buffer));
    }

    private static RequestBody parseRequestBody(final String queryString) {
        final var requestBody = new RequestBody();
        final var pairs = queryString.split("&");

        for (var pair : pairs) {
            final var keyValue = pair.split("=");
            requestBody.add(keyValue[0], keyValue[1]);
        }
        return requestBody;
    }
}
