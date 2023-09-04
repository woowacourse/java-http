package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestData {

    private static final char QUERY_STRING_DELIMITER = '?';

    private final Map<String, String> requestData;

    private RequestData(Map<String, String> requestData) {
        this.requestData = requestData;
    }

    public static RequestData of(String requestUri, String requestBody) {
        Map<String, String> queryStringParsingResult = extractQueryStrings(requestUri);
        if (requestBody.isBlank()) {
            return new RequestData(queryStringParsingResult);
        }
        Map<String, String> requestBodyParsingResult = parseQueryStrings(requestBody);

        Map<String, String> requestData = mergeParsingData(queryStringParsingResult, requestBodyParsingResult);
        return new RequestData(requestData);
    }

    private static Map<String, String> mergeParsingData(Map<String, String> queryStringParsingResult, Map<String, String> requestBodyParsingResult) {
        return Stream.of(queryStringParsingResult, requestBodyParsingResult)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue));
    }

    private static Map<String, String> extractQueryStrings(String requestUri) {
        int queryStringIndex = requestUri.indexOf(QUERY_STRING_DELIMITER);
        if (queryStringIndex == -1) {
            return new HashMap<>();
        }
        String queryStringValue = requestUri.substring(queryStringIndex + 1);
        return parseQueryStrings(queryStringValue);
    }

    private static Map<String, String> parseQueryStrings(String queryStringValue) {
        String[] queryParameters = queryStringValue.split("&");

        return Arrays.stream(queryParameters)
                .map(perQuery -> perQuery.trim().split("="))
                .collect(Collectors.toMap(
                        queryParameter -> queryParameter[0],
                        queryParameter -> queryParameter[1]
                ));
    }

    public String find(String key) {
        return requestData.get(key);
    }
}
