package org.apache.coyote.http.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http.request.exception.InvalidQueryParameterTokenException;

public class QueryParameters {

    public static final QueryParameters EMPTY = new QueryParameters(Collections.emptyMap());

    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_DELIMITER = "=";
    private static final int QUERY_PARAMETER_KEY_INDEX = 0;
    private static final int QUERY_PARAMETER_VALUE_INDEX = 1;
    private static final String JSON_PREFIX = "{";
    private static final String XML_PREFIX = "<";
    private static final String QUERY_PARAMETER_PREFIX = "?";

    private final Map<String, String> parameters;

    private QueryParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static QueryParameters fromUrlContent(final String urlContent) {
        if (isNotQueryParameterUrlContent(urlContent)) {
            return EMPTY;
        }

        final String queryParameterContent = convertQueryParameterContent(urlContent);
        final Map<String, String> parameters = convertQueryParameters(queryParameterContent);

        return new QueryParameters(parameters);
    }

    private static boolean isNotQueryParameterUrlContent(final String urlContent) {
        return urlContent == null ||
                urlContent.isEmpty() ||
                urlContent.isBlank() ||
                !urlContent.contains(QUERY_PARAMETER_PREFIX);
    }

    private static String convertQueryParameterContent(final String urlContent) {
        return urlContent.substring(urlContent.indexOf(QUERY_PARAMETER_PREFIX) + 1);
    }

    private static Map<String, String> convertQueryParameters(final String queryParameterContent) {
        return Arrays.stream(queryParameterContent.split(QUERY_PARAMETER_DELIMITER))
                     .map(param -> param.split(QUERY_PARAMETER_KEY_VALUE_DELIMITER))
                     .collect(Collectors.toMap(
                             queryParameterToken -> convertValidQueryParameterToken(
                                     queryParameterToken[QUERY_PARAMETER_KEY_INDEX]
                             ),
                             queryParameterToken -> convertValidQueryParameterToken(
                                     queryParameterToken[QUERY_PARAMETER_VALUE_INDEX]
                             )));
    }

    public static QueryParameters fromBodyContent(final String bodyContent) {
        if (isEmptyBodyContent(bodyContent)) {
            return EMPTY;
        }

        final Map<String, String> parameters = convertQueryParameters(bodyContent);

        return new QueryParameters(parameters);
    }

    private static boolean isEmptyBodyContent(final String bodyContent) {
        return bodyContent == null ||
                bodyContent.isEmpty() ||
                bodyContent.isBlank() ||
                !isStartsWithQueryParameter(bodyContent);
    }

    private static boolean isStartsWithQueryParameter(final String bodyContent) {
        return !bodyContent.startsWith(JSON_PREFIX) && !bodyContent.startsWith(XML_PREFIX);
    }

    private static String convertValidQueryParameterToken(final String queryParameterToken) {
        if (isEmptyContent(queryParameterToken)) {
            throw new InvalidQueryParameterTokenException();
        }

        return queryParameterToken.trim();
    }

    private static boolean isEmptyContent(final String content) {
        return content == null || content.isEmpty() || content.isBlank();
    }

    public String findValue(final String queryParameterKey) {
        return parameters.get(queryParameterKey);
    }

    public int size() {
        return parameters.size();
    }
}
