package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> queryParams;

    private HttpRequest(
            HttpMethod method,
            String path,
            Map<String, String> queryParams
    ) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpRequest from(String requestLine) {
        String[] tokens = requestLine.split(" ");
        String method = tokens[0];
        String path = tokens[1];

        Map<String, String> params = new HashMap<>();
        if (path.contains("?")) {
            String[] paramTokens = path.split("\\?");
            path = paramTokens[0];

            for (String param : paramTokens[1].split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    params.put(pair[0], pair[1]);
                }
            }
        }

        return new HttpRequest(HttpMethod.valueOf(method), path, params);
    }

    public boolean isMatched(HttpMethod method, String path) {
        return this.method == method && this.path.equals(path);
    }

    public boolean isGet() {
        return this.method == HttpMethod.GET;
    }

    public String getPath() {
        return path;
    }

    public String getParamValue(String param) {
        return queryParams.get(param);
    }
}
