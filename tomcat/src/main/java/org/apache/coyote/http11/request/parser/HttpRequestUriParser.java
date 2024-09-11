package org.apache.coyote.http11.request.parser;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.HttpRequestUri;

public class HttpRequestUriParser {

    private static final String QUERY_DELIMITER = "?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_DELIMITER = "=";

    private HttpRequestUriParser() {
    }

    public static HttpRequestUri parse(String requestUri) {
        int queryDelimiterIndex = requestUri.indexOf(QUERY_DELIMITER);
        if (queryDelimiterIndex == -1) {
            return new HttpRequestUri(URI.create(requestUri));
        }
        String path = requestUri.substring(0, queryDelimiterIndex);
        String query = requestUri.substring(queryDelimiterIndex + 1);
        return new HttpRequestUri(URI.create(path), getParams(query));
    }

    private static Map<String, String> getParams(String query) {
        String[] queryParams = query.split(QUERY_PARAMETER_DELIMITER);
        return Arrays.stream(queryParams)
                .map(param -> param.split(QUERY_PARAMETER_KEY_VALUE_DELIMITER))
                .collect(Collectors.toUnmodifiableMap(split -> split[0], split -> split[1]));
    }
}
