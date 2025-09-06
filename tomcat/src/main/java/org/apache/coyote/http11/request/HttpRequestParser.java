package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestParser {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);

    public Http11Request getHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final var requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            return null;
        }
        final var requestLines = requestLine.split(" ");
        final var resourcePath = requestLines[1];
        final var headers = getHeaders(bufferedReader);

        log.info("resourcePath: {}, header: {}", resourcePath, headers);
        return new Http11Request(requestLines[0], resourcePath, headers, null);
    }

    private LinkedHashMap<String, String> getHeaders(final BufferedReader bufferedReader) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            final var header = line.split(":", 2);
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            }
        }
        return headers;
    }
}
