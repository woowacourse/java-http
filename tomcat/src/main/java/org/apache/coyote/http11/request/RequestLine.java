package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int QUERY_PARAMETER_ENTRY_SIZE = 2;
    private static final int QUERY_PARAMETER_KEY_INDEX = 0;
    private static final int QUERY_PARAMETER_VALUE_INDEX = 1;

    private String method;
    private String path;
    private Map<String, String> queryParameters;

    public static RequestLine create(String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");
        String method = splitRequestLine[METHOD_INDEX];
        String path = splitRequestLine[PATH_INDEX];

        String queryString = "";
        int queryStringIndex = requestLine.indexOf("?");
        if (queryStringIndex >= 0) {
            path = requestLine.substring(0, queryStringIndex);
            queryString = requestLine.substring(queryStringIndex)
                    .replace("?", "");
        }
        Map<String, String> queryParameters = parseQueryParameters(queryString);

        return new RequestLine(method, path, queryParameters);
    }

    private static Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        Arrays.stream(queryString.split("&"))
                .forEach(queryParameterEntry -> {
                    String[] split = queryParameterEntry.split("=");
                    if (split.length == QUERY_PARAMETER_ENTRY_SIZE
                            && !split[QUERY_PARAMETER_KEY_INDEX].isBlank()
                            && !split[QUERY_PARAMETER_VALUE_INDEX].isBlank()) {
                        queryParameters.put(split[QUERY_PARAMETER_KEY_INDEX], split[QUERY_PARAMETER_VALUE_INDEX]);
                    }
                });
        return queryParameters;
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
