package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestLine {

    private static final int REQUEST_LINE_TOKEN_COUNT = 3;

    private final HttpMethod method;
    private final String path;
    private final String protocol;
    private final Map<String, String> queryParameters;

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ", REQUEST_LINE_TOKEN_COUNT);
        if (tokens.length != REQUEST_LINE_TOKEN_COUNT) {
            throw new IllegalArgumentException("Invalid request line: " + requestLine);
        }

        this.method = HttpMethod.from(tokens[0]);
        this.protocol = tokens[2];

        int queryStartIndex = tokens[1].indexOf('?');
        if (queryStartIndex < 0) {
            this.path = tokens[1];
            this.queryParameters = Collections.emptyMap();
            return;
        }

        this.path = tokens[1].substring(0, queryStartIndex);
        this.queryParameters = parseParameters(tokens[1].substring(queryStartIndex + 1));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getParameter(String name) {
        return queryParameters.get(name);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    static Map<String, String> parseParameters(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, String> parameters = new LinkedHashMap<>();
        for (String pair : value.split("&")) {
            String[] nameAndValue = pair.split("=", 2);
            String name = decode(nameAndValue[0]);
            String parameterValue = nameAndValue.length == 2 ? decode(nameAndValue[1]) : "";
            parameters.put(name, parameterValue);
        }
        return Collections.unmodifiableMap(parameters);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
