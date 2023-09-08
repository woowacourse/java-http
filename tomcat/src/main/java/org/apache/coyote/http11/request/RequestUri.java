package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestUri {

    private static final String QUERY_STRING_SIGN = "?";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String FIELD_AND_VALUE_DELIMITER = "=";
    private static final int QUERY_STRING_FIELD_INDEX = 0;
    private static final int QUERY_STRING_VALUE_INDEX = 1;

    private final String uri;
    private final Map<String, String> queryStringsMap;

    public RequestUri(String uri, Map<String, String> queryStringsMap) {
        this.uri = uri;
        this.queryStringsMap = queryStringsMap;
    }

    public static RequestUri from(String requestUri) {
        Map<String, String> queryStringsMap = new HashMap<>();

        if (requestUri.contains(QUERY_STRING_SIGN)) {
            int signIndex = requestUri.indexOf(QUERY_STRING_SIGN);
            String queryStrings = requestUri.substring(signIndex + 1);
            requestUri = requestUri.substring(0, signIndex);

            queryStringsMap = Arrays.stream(queryStrings.split(QUERY_STRING_DELIMITER))
                    .map(queryString -> queryString.split(FIELD_AND_VALUE_DELIMITER))
                    .collect(Collectors.toMap(
                            fieldAndValue -> fieldAndValue[QUERY_STRING_FIELD_INDEX],
                            fieldAndValue -> fieldAndValue[QUERY_STRING_VALUE_INDEX],
                            (prev, update) -> update
                    ));
        }
        return new RequestUri(requestUri, queryStringsMap);
    }

    public boolean consistsOf(String uri) {
        return Objects.equals(this.uri, uri);
    }

    public boolean hasQueryString() {
        return !queryStringsMap.isEmpty();
    }

    public String getQueryStringValue(String field) {
        return queryStringsMap.get(field);
    }

    public String uri() {
        return uri;
    }

    public Map<String, String> queryStringsMap() {
        return queryStringsMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestUri that = (RequestUri) o;
        return Objects.equals(uri, that.uri) && Objects.equals(queryStringsMap, that.queryStringsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, queryStringsMap);
    }

    @Override
    public String toString() {
        return "RequestUri{" +
                "uri='" + uri + '\'' +
                ", queryStringsMap=" + queryStringsMap +
                '}';
    }
}
