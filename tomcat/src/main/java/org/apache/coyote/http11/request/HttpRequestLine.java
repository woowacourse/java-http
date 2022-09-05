package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    public static final int METHOD_INDEX = 0;
    public static final int URI_INDEX = 1;
    public static final int VERSION_INDEX = 2;
    public static final int QUERY_PARAM_NAME_INDEX = 0;
    public static final int QUERY_PARAM_VALUE_INDEX = 1;

    private final HttpMethod httpMethod;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    public HttpRequestLine(final HttpMethod httpMethod, final String path, final String httpVersion,
                           final Map<String, String> queryParams) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.queryParams = queryParams;
    }

    public static HttpRequestLine of(final String rawRequestLine) {
        final String[] parsedLine = rawRequestLine.split(" ");

        final String method = parsedLine[METHOD_INDEX];
        final String uri = parsedLine[URI_INDEX];
        final String httpVersion = parsedLine[VERSION_INDEX];

        return new HttpRequestLine(HttpMethod.of(method), parseToPath(uri), httpVersion, parseToQueryParams(uri));
    }

    private static String parseToPath(final String uri) {
        return uri.split("\\?")[0];
    }

    private static Map<String, String> parseToQueryParams(final String uri) {
        final Map<String, String> queryParams = new HashMap<>();
        if (hasQueryString(uri)) {
            final int index = uri.indexOf("?");
            parseQueryString(queryParams, getQueryString(uri, index));
        }

        return queryParams;
    }

    private static boolean hasQueryString(final String uri) {
        return uri.contains("?");
    }

    private static String getQueryString(final String uri, final int index) {
        return uri.substring(index + 1);
    }

    private static void parseQueryString(final Map<String, String> queryParams, final String queryString) {
        final String[] queries = queryString.split("&");

        for (final String queryParam : queries) {
            final String[] parsedQuery = queryParam.split("=");
            queryParams.put(parsedQuery[QUERY_PARAM_NAME_INDEX], parsedQuery[QUERY_PARAM_VALUE_INDEX]);
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getQueryParams() {
        return new HashMap<>(queryParams);
    }
}
