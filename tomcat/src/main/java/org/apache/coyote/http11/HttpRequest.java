package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> requestHeaders = new HashMap<>();
    private final String requestBody;

    public HttpRequest(final BufferedReader reader) throws IOException {
        final String firstLine = reader.readLine();
        if (firstLine == null) {
            throw new IOException();
        }
        this.requestLine = new RequestLine(firstLine);

        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            final String[] headerParts = line.split(": ", 2);
            if(headerParts.length == 2) {
                requestHeaders.put(headerParts[0], headerParts[1]);
            }
        }

        this.requestBody = parseBody(reader);
    }

    private String parseBody(final BufferedReader reader) throws IOException {
        if ("POST".equals(requestLine.getMethod()) && requestHeaders.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return requestHeaders.get(name);
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Map<String, String> getParameters() {
        final Map<String, String> parameters = new HashMap<>();
        final String formData = getRequestBody();
        if (formData != null && !formData.isBlank()) {
            final String[] pairs = formData.split("&");
            for (final String pair : pairs) {
                final String[] keyValue = pair.split("=");
                if (keyValue.length >= 2) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    parameters.put(key, value);
                }
            }
        }
        return parameters;
    }
}
