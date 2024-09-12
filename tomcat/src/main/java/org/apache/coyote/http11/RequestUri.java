package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestUri {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String EMPTY_STRING = "";
    private static final String QUERY_PARAMS_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

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
        List<String> queryParams = Arrays.asList(queryString.split(QUERY_PARAMS_DELIMITER));
        Map<String, String> queryParameters = queryParams.stream()
                .map(query -> Arrays.asList(query.split(KEY_VALUE_DELIMITER)))
                .collect(Collectors.toMap(List::getFirst, RequestUri::getParamValue));

        return new RequestUri(path, queryString, queryParameters);
    }

    private static String getParamValue(List<String> param) {
        if (param.size() > 1) {
            return param.get(1);
        }
        return EMPTY_STRING;
    }

    public String getRequestUri() {
        if (queryString.isEmpty()) {
            return path;
        }
        return String.join(QUERY_STRING_DELIMITER, path, queryString);
    }

    public String getPath() {
        return path;
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
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }

    @Override
    public String toString() {
        return "RequestUri{" +
                "path='" + path + '\'' +
                ", queryString='" + queryString + '\'' +
                ", queryParameters=" + queryParameters +
                '}';
    }
}
