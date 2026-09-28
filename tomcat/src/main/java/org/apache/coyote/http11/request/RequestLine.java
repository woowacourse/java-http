package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private HttpMethod method;
    private ParsedTarget parsedTarget;
    private String protocolVersion;

    public RequestLine(String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");

        this.method = HttpMethod.from(splitRequestLine[0]);
        this.parsedTarget = parseRequestTarget(splitRequestLine[1]);
        this.protocolVersion = splitRequestLine[2];
    }

    public HttpMethod method() {
        return method;
    }

    public String path() {
        return parsedTarget.path();
    }

    public boolean matchPath(String path) {
        return parsedTarget.matchesPath(path);
    }

    private ParsedTarget parseRequestTarget(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf("?");

        if (queryStartIndex < 0) {
            return new ParsedTarget(
                    requestTarget,
                    new HashMap<>()
            );
        }

        String path = requestTarget.substring(0, queryStartIndex);
        String queryString = requestTarget.substring(queryStartIndex + 1);

        return new ParsedTarget(path, parseUrlEncodedParameters(queryString));
    }

    private Map<String, String> parseUrlEncodedParameters(String encodedParameters) {
        Map<String, String> parameters = new HashMap<>();

        if (encodedParameters == null || encodedParameters.isEmpty()) {
            return parameters;
        }

        for (String parameter : encodedParameters.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                parameters.put(key, value);
            }
        }

        return parameters;
    }
}
