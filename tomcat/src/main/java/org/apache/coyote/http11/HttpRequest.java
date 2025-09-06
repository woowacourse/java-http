package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String requestMethod;
    private final String uri;
    private final Map<String, String> query;

    public HttpRequest(final InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("올바르지 않은 요청입니다.");
        }
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            throw new IOException("올바르지 않은 요청입니다.");
        }
        this.requestMethod = parts[0];
        if (parts[1].contains("?")) {
            int index = parts[1].indexOf("?");
            this.uri = parts[1].substring(0, index);
            String queryString = parts[1].substring(index + 1);
            query = makeQuery(queryString);
        } else {
            this.uri = parts[1];
            query = Map.of();
        }
    }

    private Map<String, String> makeQuery(String queryString) {
        Map<String, String> query = new HashMap<>();
        String[] items = queryString.split("&");
        for (String item : items) {
            String[] i = item.split("=");
            query.put(i[0], i[1]);
        }
        return query;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQuery() {
        return query;
    }
}
