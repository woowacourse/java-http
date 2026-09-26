package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String PARAM_DELIMITER = "&";
    private static final String PARAM_EQUAL = "=";

    private final RequestLine requestLine;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();

    public HttpRequest(BufferedReader reader) throws IOException {
        this.requestLine = new RequestLine(reader.readLine());
        readHeaders(reader);
        readParameters(reader);
    }

    private void readHeaders(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                headers.put(parts[0].trim().toLowerCase(), parts[1].trim());
            }
        }
    }

    private void readParameters(BufferedReader reader) throws IOException {
        if (!getMethod().equals("POST")) {
            return;
        }

        String body = readRequestBody(reader);
        this.parameters.putAll(parseParams(body));
    }

    private String readRequestBody(BufferedReader reader) throws IOException {
        StringBuilder requestBody = new StringBuilder();

        int contentLength = getContentLength();
        for (int i = 0; i < contentLength; i++) {
            int character = reader.read();
            if (character == -1) {
                break;
            }
            requestBody.append((char) character);
        }

        return requestBody.toString();
    }

    private Map<String, String> parseParams(String params) {
        Map<String, String> parsedParams = new HashMap<>();
        if (params.isBlank()) {
            return parsedParams;
        }

        for (String param : params.split(PARAM_DELIMITER)) {
            String[] pair = param.split(PARAM_EQUAL, 2);
            if (pair.length != 2) {
                continue;
            }

            String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            parsedParams.put(key, value);
        }

        return parsedParams;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return Map.copyOf(parameters);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return headers.getOrDefault(name.toLowerCase(), "");
    }

    public int getContentLength() {
        String contentLength = getHeader("Content-Length");

        if (contentLength.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(contentLength);
    }
}
