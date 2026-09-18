package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private static final String EMPTY_LINE = "\\r\\n\\r\\n";

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> body;

    public HttpRequest(String rawHttpRequest) {
        String[] headerAndBody = rawHttpRequest.split(EMPTY_LINE);
        String header = headerAndBody[0];

        String requestLine = header.split(System.lineSeparator())[0];
        String[] requestLineParts = requestLine.split(" ", 3);
        this.method = requestLineParts[0];

        String uri = requestLineParts[1];
        this.path = extractPath(uri);
        this.queryParams = extractQueryParams(uri);

        String body = headerAndBody.length < 2 ? "" : headerAndBody[1];
        this.body = parseEncodedFormData(body);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getBody() {
        return body;
    }

    private String extractPath(String uri) {
        if (!uri.contains("?")) {
            return uri;
        }

        int indexOfQueryDelimiter = uri.indexOf("?");
        return uri.substring(0, indexOfQueryDelimiter);
    }

    private Map<String, String> extractQueryParams(String uri) {
        if (!uri.contains("?")) {
            return Map.of();
        }

        int indexOfQueryDelimiter = uri.indexOf("?");
        String rawParams = uri.substring(indexOfQueryDelimiter + 1);
        if (rawParams.isEmpty()) {
            return Map.of();
        }

        Map<String, String> params = new LinkedHashMap<>();
        for (String rawParam : rawParams.split("&")) {
            if (rawParam.isEmpty()) {
                continue;
            }

            String[] nameAndValue = rawParam.split("=", 2);
            if (nameAndValue.length < 2) {
                params.put(nameAndValue[0], "");
            } else {
                params.put(nameAndValue[0], nameAndValue[1]);
            }
        }
        return params;
    }

    private Map<String, String> parseEncodedFormData(String rawBody) {
        if (rawBody.isEmpty()) {
            return Map.of();
        }

        Map<String, String> formData = new LinkedHashMap<>();
        for (String rawField : rawBody.split("&")) {
            if (rawField.isEmpty()) {
                continue;
            }

            String[] nameAndValue = rawField.split("=", 2);
            String name = URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8);
            String value = nameAndValue.length < 2
                    ? ""
                    : URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8);
            formData.put(name, value);
        }
        return formData;
    }
}
