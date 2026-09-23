package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

class HttpRequestParser {

    private static final String CONTENT_LENGTH = "content-length";
    private static final String CONTENT_TYPE = "content-type";

    private final InputStream inputStream;

    HttpRequestParser(InputStream inputStream) {
        this.inputStream = new BufferedInputStream(inputStream);
    }

    HttpRequest parse() throws IOException {
        String firstLine = readLine();
        if (firstLine == null) {
            throw new IllegalArgumentException("Missing HTTP request line");
        }

        RequestLine requestLine = new RequestLine(firstLine);
        Map<String, String> headers = readHeaders();
        byte[] body = readBody(headers);
        FormParameters formParameters = new FormParameters(headers.get(CONTENT_TYPE), body);
        return new HttpRequest(requestLine, headers, body, formParameters);
    }

    private Map<String, String> readHeaders() throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = readLine()) != null && !line.isEmpty()) {
            addHeader(headers, line);
        }
        return headers;
    }

    private void addHeader(Map<String, String> headers, String line) {
        int separatorIndex = line.indexOf(':');
        if (separatorIndex < 0) {
            throw new IllegalArgumentException("Invalid HTTP header: " + line);
        }

        String name = line.substring(0, separatorIndex).trim().toLowerCase(Locale.ROOT);
        String value = line.substring(separatorIndex + 1).trim();
        headers.put(name, value);
    }

    private byte[] readBody(Map<String, String> headers) throws IOException {
        int contentLength = contentLength(headers);
        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IllegalArgumentException("Request body is shorter than Content-Length");
        }
        return body;
    }

    private int contentLength(Map<String, String> headers) {
        String value = headers.get(CONTENT_LENGTH);
        if (value == null) {
            return 0;
        }

        int contentLength = Integer.parseInt(value);
        if (contentLength < 0) {
            throw new IllegalArgumentException("Content-Length must not be negative");
        }
        return contentLength;
    }

    private String readLine() throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int current;
        while ((current = inputStream.read()) != -1) {
            if (current == '\n') {
                break;
            }
            if (current != '\r') {
                line.write(current);
            }
        }
        if (current == -1 && line.size() == 0) {
            return null;
        }
        return line.toString(StandardCharsets.UTF_8);
    }

}
