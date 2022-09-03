package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestStartLine {

    private static final int INDEX_OF_METHOD = 0;
    private static final int INDEX_OF_URI = 1;
    private static final int INDEX_OF_VERSION = 2;

    private static final char START_QUERY_PARAM = '?';
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_MAPPER = "=";
    private static final int INDEX_OF_QUERY_KEY = 0;
    private static final int INDEX_OF_QUERY_VALUE = 1;

    private final String method;
    private final String uri;
    private final Map<String, String> queryParam;
    private final String version;

    private HttpRequestStartLine(String method, String uri, Map<String, String> queryParam, String version) {
        this.method = method;
        this.uri = uri;
        this.queryParam = queryParam;
        this.version = version;
    }

    public static HttpRequestStartLine from(String line) {
        String[] parsedLine = line.split(" ");

        String method = parsedLine[INDEX_OF_METHOD];
        String uri = parsedLine[INDEX_OF_URI];
        String version = parsedLine[INDEX_OF_VERSION];

        if (uri.contains(String.valueOf(START_QUERY_PARAM))) {
            Map<String, String> queryParam = parseQueryParam(uri);
            uri = uri.substring(0, uri.indexOf(START_QUERY_PARAM));

            return new HttpRequestStartLine(method, uri, queryParam, version);
        }

        return new HttpRequestStartLine(method, uri, new HashMap<>(), version);
    }

    private static Map<String, String> parseQueryParam(String uri) {
        String queryString = uri.substring(uri.indexOf(START_QUERY_PARAM) + 1);
        Map<String, String> queryParam = new HashMap<>();

        for (String q : queryString.split(QUERY_PARAM_DELIMITER)) {
            String[] parsedQuery = q.split(QUERY_PARAM_MAPPER);
            queryParam.put(parsedQuery[INDEX_OF_QUERY_KEY], parsedQuery[INDEX_OF_QUERY_VALUE]);
        }

        return queryParam;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
