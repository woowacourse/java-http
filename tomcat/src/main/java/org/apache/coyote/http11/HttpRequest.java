package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private Map<String, String> queries;
    private Map<String, String> headers;
    private String requestBody;

    private HttpRequest(String method, String path, Map<String, String> queries, Map<String, String> headers, String requestBody) {
        this.method = method;
        this.path = path;
        this.queries = queries;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest parseFrom(BufferedReader br) throws IOException {
        String line = br.readLine();

        if (line == null || line.isBlank()) {
            throw new IOException("요청이 없습니다.");
        }

        String[] request = line.split(" ");

        String method = request[0];
        String uri = request[1];
        Map<String, String> queryMap = new HashMap<>();
        int index = uri.indexOf("?");

        if (index != -1) {
            queryMap = splitQuery(uri.substring(index + 1));
            uri = uri.substring(0, index);
        }

        Map<String, String> headerMap = new HashMap<>();
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerToken = line.split(":", 2);
            if (headerToken.length == 2) {
                headerMap.put(headerToken[0].trim(), headerToken[1].trim());
            }
        }

        String body = readBody(br, headerMap);

        return new HttpRequest(method, uri, queryMap, headerMap, body);
    }

    private static Map<String, String> splitQuery(String queryString) {
        String[] queries = queryString.split("\\&");
        Map<String, String> queryMap = new HashMap<>();

        for (String query : queries) {
            String[] keyAndValue = query.split("=", 2);
            queryMap.put(keyAndValue[0], keyAndValue[1]);
        }

        return queryMap;
    }

    private static String readBody(BufferedReader br, Map<String, String> headers) throws IOException {
        String contentLength = headers.get("Content-Length");

        if (contentLength == null) {
            return "";
        }

        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];

        int totalRead = 0;

        while (totalRead < length) {
            int read = br.read(
                    buffer,
                    totalRead,
                    length - totalRead
            );

            if (read == -1) {
                throw new IOException("본문이 Content-Length보다 짧습니다.");
            }

            totalRead += read;
        }

        return new String(buffer);
    }

    public Map<String, String> getFormParameters() {
        Map<String, String> parameters = new HashMap<>();

        if (this.getRequestBody().isBlank()) {
            return parameters;
        }

        String[] splitParameters = this.getRequestBody().split("&");
        for (String token : splitParameters) {
            String[] keyAndValue = token.split("=", 2);
            if (keyAndValue.length == 2) {
                String key = URLDecoder.decode(
                        keyAndValue[0],
                        StandardCharsets.UTF_8
                );

                String value = URLDecoder.decode(
                        keyAndValue[1],
                        StandardCharsets.UTF_8
                );

                parameters.put(key, value);
            }
        }

        return parameters;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
