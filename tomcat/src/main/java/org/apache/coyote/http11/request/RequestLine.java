package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpVersion;

public class RequestLine {

    private static final String QUERY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";
    private static final String QUERY_START = "\\?";
    private static final String SPACE_DELIMITER = " ";
    private static final String URL_NO_QUERY = "?";
    private static final int QUERY_LINE_INDEX = 1;
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final String path;
    private final String url;
    private final HttpVersion version;
    private final Map<String, String> queryParams;

    public RequestLine(
            HttpMethod method,
            String path,
            String url,
            HttpVersion version,
            Map<String, String> queryParams
    ) {
        this.method = method;
        this.path = path;
        this.url = url;
        this.version = version;
        this.queryParams = queryParams;
    }

    public static RequestLine from(String requestLine) {
        String[] requestLineToken = requestLine.split(SPACE_DELIMITER);

        HttpMethod method = HttpMethod.from(requestLineToken[METHOD_INDEX]);
        String url = requestLineToken[URL_INDEX];
        HttpVersion version = HttpVersion.from(requestLineToken[VERSION_INDEX]);
        String path = parsePath(url);
        Map<String, String> queryParams = parseQueryParameter(url);

        return new RequestLine(method, path, url, version, queryParams);
    }

    private static String parsePath(String url) {
        if (!url.contains(URL_NO_QUERY)) {
            return url;
        }
        String[] urlParts = url.split(QUERY_START);
        return urlParts[0];
    }

    private static Map<String, String> parseQueryParameter(String url) {
        Map<String, String> queryParams = new HashMap<>();
        if (!url.contains(QUERY_START.substring(1))) {
            return queryParams;
        }
        String[] urlParts = url.split(QUERY_START);
        String queryLine = urlParts[QUERY_LINE_INDEX];
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

    public HttpVersion getVersion() {
        return version;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
