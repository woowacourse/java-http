package org.apache.coyote.http11.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.dto.HttpRequestUrl;

public class HttpRequestHandler {

    private static final int KEY_AND_VALUE_COUNT = 2;
    private static final String HTTP_HEADER_DELIMITER = ":";
    private static final String HTTP_URL_DELIMITER = " ";

    public HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestUrl httpRequestUrl = readUrl(reader);
        Map<String, String> headers = readHeaders(reader);
        return new HttpRequest(httpRequestUrl.method(), httpRequestUrl.path(), httpRequestUrl.version(), headers);
    }

    private HttpRequestUrl readUrl(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        String[] parts = requestLine.split(HTTP_URL_DELIMITER);
        String method = parts[0];
        String path = parts[1];
        String version = parts[2];
        return new HttpRequestUrl(method, path, version);
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (hasMoreHeader(line = reader.readLine())) {
            parseHeaderLine(line).ifPresent(e -> headers.put(e.getKey(), e.getValue()));
        }
        return headers;
    }

    private boolean hasMoreHeader(String line) {
        return line != null && !line.isEmpty();
    }

    private Optional<Entry<String, String>> parseHeaderLine(String line) {
        String[] parts = line.split(HTTP_HEADER_DELIMITER, KEY_AND_VALUE_COUNT);
        if (parts.length != KEY_AND_VALUE_COUNT) {
            return Optional.empty();
        }
        return Optional.of(Map.entry(parts[0].strip(), parts[1].strip()));
    }
}
