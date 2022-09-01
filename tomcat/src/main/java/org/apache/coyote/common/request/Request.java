package org.apache.coyote.common.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.common.HttpVersion;

public class Request {

    private static final int METHOD = 0;
    private static final int URL = 1;
    private static final int HTTP_VERSION = 2;
    private static final String PATH_QUERY_STRING_DELIMITER = "?";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";

    private final HttpVersion httpVersion;
    private final RequestMethod method;
    private String path;
    private Map<String, String> queryString;

    public Request(final String request) {
        final String[] parsedRequest = request.split(" ");
        this.method = RequestMethod.of(parsedRequest[METHOD]);
        this.httpVersion = HttpVersion.of(parsedRequest[HTTP_VERSION]);
        parseUrlToPathAndQueryString(parsedRequest[URL]);
    }

    private void parseUrlToPathAndQueryString(final String url) {
        final int queryStringDelimiterIndex = url.indexOf(PATH_QUERY_STRING_DELIMITER);
        if (queryStringDelimiterIndex < 0) {
            this.path = url;
            this.queryString = new HashMap<>();
            return;
        }
        this.path = url.substring(0, queryStringDelimiterIndex);
        this.queryString = parseRawQueryString(url.substring(queryStringDelimiterIndex + 1));
    }

    private Map<String, String> parseRawQueryString(final String rawQueryString) {
        final String[] parsedQueryString = rawQueryString.split(QUERY_STRING_DELIMITER);
        return Arrays.stream(parsedQueryString)
                .map(qs -> qs.split(QUERY_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }

    public String getPath() {
        return path;
    }

    public String getQueryStringValue(final String key) {
        return queryString.getOrDefault(key, "");
    }
}
