package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;
    private final HttpCookie cookies;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new BadRequestException("잘못된 요청 라인입니다.");
        }
        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new BadRequestException("잘못된 요청 라인입니다.");
        }
        this.method = tokens[0];
        String uri = tokens[1];
        if (uri.contains("?")) {
            int queryIndex = uri.indexOf("?");
            this.path = uri.substring(0, queryIndex);
            String queryString = uri.substring(queryIndex + 1);
            this.queryParams = parseQueryString(queryString);
        } else {
            this.path = uri;
            this.queryParams = new HashMap<>();
        }

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }

        if ("POST".equals(method)) {
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            char[] buffer = new char[contentLength];
            int read = reader.read(buffer, 0, contentLength);
            this.body = new String(buffer, 0, read);

            if ("application/x-www-form-urlencoded".equalsIgnoreCase(headers.get("Content-Type"))) {
                queryParams.putAll(parseQueryString(body));
            }
        } else {
            this.body = null;
        }
        this.cookies = new HttpCookie(headers.get("Cookie"));
    }

    private Map<String, String> parseQueryString(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return Map.of();
        }
        Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                params.put(keyValue[0], "");
            }
        }
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getBody() {
        return body;
    }

    public HttpCookie getCookies() {
        return cookies;
    }
}
