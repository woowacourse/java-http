package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final String QUERY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";
    private static final String QUERY_START = "\\?";

    private final HttpMethod method;
    private final String path;
    private final String url;
    private final String version;
    private final Map<String, String> queryParams;

    public RequestLine(HttpMethod method, String path, String url, String version, Map<String, String> queryParams) {
        this.method = method;
        this.path = path;
        this.url = url;
        this.version = version;
        this.queryParams = queryParams;
    }

    public static RequestLine from(String requestLine) {
        String[] requestLineToken = requestLine.split(" ");

        HttpMethod method = HttpMethod.from(requestLineToken[0]);
        String url = requestLineToken[1];
        String version = requestLineToken[2];
        String path = parsePath(url);
        Map<String, String> queryParams = parseQueryParameter(url);

        return new RequestLine(method, path, url, version, queryParams);
    }

    private static String parsePath(String url) {
        if (!url.contains("?")) {
            return url;
        }
        String[] urlParts = url.split("\\?");
        return urlParts[0];
    }

    private static Map<String, String> parseQueryParameter(String url) {
        Map<String, String> queryParams = new HashMap<>();
        if (!url.contains("?")) {
            return queryParams;
        }
        String[] urlParts = url.split(QUERY_START);
        String queryLine = urlParts[1];
        String[] queryList = queryLine.split(QUERY_DELIMITER);
        for (String query : queryList) {
            String[] queryParam = query.split(PARAM_DELIMITER);
            queryParams.put(queryParam[0], queryParam[1]);
        }

        return queryParams;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
