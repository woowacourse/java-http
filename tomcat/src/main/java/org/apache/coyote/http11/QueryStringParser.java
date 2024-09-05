package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class QueryStringParser {
    private static final String QUERY_STRING = "?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String ELEMENT_DELIMITER = "=";
    private static final int PARAMETER_INDEX = 0;
    private static final int PARAMETER_VALUE_INDEX = 1;

    public boolean isQueryString(String requestUri) {
        return requestUri.contains(QUERY_STRING);
    }

    public String parseUri(String requestUri) {
        return requestUri.substring(0, findQueryStringIndex(requestUri));
    }

    public Map<String, String> parseParameters(String requestUri) {
        if (!isQueryString(requestUri)) {
            throw new IllegalArgumentException("쿼리 스트링 요청이 아닙니다.");
        }

        Map<String, String> parameters = new HashMap<>();
        String queryString = parseQueryString(requestUri);
        String[] elements = queryString.split(PARAMETER_DELIMITER);

        Stream.of(elements)
                .forEach(element -> parseElement(parameters, element));

        return parameters;
    }

    private void parseElement(Map<String, String> parameters, String element) {
        if (!element.contains(ELEMENT_DELIMITER)) {
            throw new IllegalArgumentException("잘못된 쿼리 파라미터입니다.");
        }

        String[] parsedElement = element.split(ELEMENT_DELIMITER);
        String key = parsedElement[PARAMETER_INDEX];
        String value = parsedElement[PARAMETER_VALUE_INDEX];
        parameters.put(key, value);
    }

    private String parseQueryString(String requestUri) {
        return requestUri.substring(findQueryStringIndex(requestUri) + 1);
    }

    private int findQueryStringIndex(String requestUri) {
        return requestUri.indexOf(QUERY_STRING);
    }
}
