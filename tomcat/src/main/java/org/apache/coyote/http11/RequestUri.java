package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestUri {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String EMPTY_STRING = "";

    private final String path;
    private final String queryString;
    private final Map<String, String> queryParameters;

    private RequestUri(String path, String queryString, Map<String, String> queryParameters) {
        this.path = path;
        this.queryString = queryString;
        this.queryParameters = queryParameters;
    }

    public static RequestUri from(String requestUri) {
        int index = requestUri.indexOf(QUERY_STRING_DELIMITER);
        if (index == -1) {
            return new RequestUri(requestUri, EMPTY_STRING, Map.of());
        }
        String path = requestUri.substring(0, index);
        String queryString = requestUri.substring(index + 1);
        List<String> queryParams = Arrays.asList(queryString.split("&"));
        Map<String, String> queryParameters = queryParams.stream()
                .map(query -> Arrays.asList(query.split("=")))
                .collect(Collectors.toMap(List::getFirst, List::getLast));

        return new RequestUri(path, queryString, queryParameters);
    }

    public boolean hasQueryParameters() {
        return !queryParameters.isEmpty();
    }

    public String getRequestUri() {
        if (queryString.isEmpty()) {
            return path;
        }
        return String.join(QUERY_STRING_DELIMITER, path, queryString);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }
}
