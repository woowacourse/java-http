package org.apache.coyote.util.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        this.method = tokens[0];

        String[] uriTokens = tokens[1].split("\\?");
        this.path = uriTokens[0];
        this.queryParams = parseQueryParams(uriTokens.length > 1 ? uriTokens[1] : null);
    }

    private Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (queryString == null) {
            return queryParams;
        }
        Arrays.stream(queryString.split("&"))
                .map(param -> param.split("="))
                .forEach(p -> queryParams.put(p[0], p.length > 1 ? p[1] : ""));
        return queryParams;
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
}
