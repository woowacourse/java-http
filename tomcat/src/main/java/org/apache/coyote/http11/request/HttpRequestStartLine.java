package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.common.QueryParser;

public class HttpRequestStartLine {

    private static final int INDEX_OF_METHOD = 0;
    private static final int INDEX_OF_URI = 1;
    private static final int INDEX_OF_VERSION = 2;

    private static final char START_QUERY_PARAM = '?';

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
            String query = uri.substring(uri.indexOf(START_QUERY_PARAM));
            uri = uri.substring(0, uri.indexOf(START_QUERY_PARAM));

            return new HttpRequestStartLine(method, uri, QueryParser.parse(query), version);
        }

        return new HttpRequestStartLine(method, uri, new HashMap<>(), version);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
