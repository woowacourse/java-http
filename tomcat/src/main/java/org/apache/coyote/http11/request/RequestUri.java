package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestUri {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_URI_INDEX = 1;
    private static final String QUERY_STRING_BEGIN_DELIMITER = "?";
    private static final String QUERY_STRING_KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_STRING_UNIT_DELIMITER = "&";

    private final String requestUri;

    private RequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public static RequestUri from(String requestLine) {
        return new RequestUri(getRequestUri(requestLine));
    }

    private static String getRequestUri(String requestLine) {
        List<String> httpRequestMethodInformation = Arrays.stream(requestLine
                .split(REQUEST_LINE_DELIMITER))
                .collect(Collectors.toList());
        return httpRequestMethodInformation.get(REQUEST_URI_INDEX);
    }

    private Map<String, String> getQueryString(String requestUri) {
        int index = requestUri.indexOf(QUERY_STRING_BEGIN_DELIMITER);
        String queryString = requestUri.substring(index + 1);
        String[] dividedQueryString = queryString.split(QUERY_STRING_UNIT_DELIMITER);

        return Arrays.stream(dividedQueryString)
                .map(query -> query.split(QUERY_STRING_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0].trim(), value -> value[1].trim()));
    }

    public boolean isExistQueryString() {
        return requestUri.contains(QUERY_STRING_BEGIN_DELIMITER);
    }

    public String getQueryStringValue(String key) {
        final Map<String, String> queryStrings = getQueryString(requestUri);
        if(queryStrings.containsKey(key)) {
            return queryStrings.get(key);
        }
        return "";
    }

    public String getRequestUri() {
        return requestUri;
    }

    public boolean containsUri(String uri) {
        return requestUri.contains(uri);
    }
}
