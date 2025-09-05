package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest {

    private final String path;
    private final Map<String, String> queryParams;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = reader.readLine();

        if (requestLine == null) {
            throw new IOException("잘못된 요청 라인입니다.");
        }
        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new IOException("잘못된 요청 라인입니다.");
        }
        String uri = tokens[1];

        if (uri.contains("?")) {
            int queryIndex = uri.indexOf("?");
            this.path = uri.substring(0, queryIndex);
            String queryString = uri.substring(queryIndex + 1);
            this.queryParams = parseQueryString(queryString);
        } else {
            this.path = uri;
            this.queryParams = Map.of();
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        if (queryString == null) {
            return Map.of();
        }
        Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }
}
