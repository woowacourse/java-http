package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.utils.Separator;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final String QUERY_STRING_STARTING_SIGN = "?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private String method;
    private String path;
    private Map<String, String> queryParameters;

    public static RequestLine create(String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");
        String method = splitRequestLine[METHOD_INDEX];
        String path = splitRequestLine[PATH_INDEX];

        String queryString = "";
        int queryStringIndex = requestLine.indexOf(QUERY_STRING_STARTING_SIGN);
        if (queryStringIndex >= 0) {
            path = requestLine.substring(0, queryStringIndex);
            queryString = requestLine.substring(queryStringIndex)
                    .replace(QUERY_STRING_STARTING_SIGN, "");
        }

        return new RequestLine(method, path, parseQueryParameters(queryString));
    }

    private static Map<String, String> parseQueryParameters(String queryString) {
        if (StringUtils.isBlank(queryString)) {
            return Map.of();
        }

        List<String> parameterEntries = List.of(queryString.split(QUERY_PARAMETER_SEPARATOR));
        Map<String, String> parameters = Separator.separateKeyValueBy(parameterEntries, KEY_VALUE_SEPARATOR);

        return parameters;
    }

    private RequestLine(String method, String path, Map<String, String> queryParameters) {
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public boolean isGetRequest() {
        return "GET".equals(method);
    }

    public boolean isPostRequest() {
        return "POST".equals(method);
    }

    public String getPath() {
        return this.path;
    }
}
