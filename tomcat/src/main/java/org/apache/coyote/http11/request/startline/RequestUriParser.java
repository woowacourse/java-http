package org.apache.coyote.http11.request.startline;

import java.util.HashMap;
import java.util.Map;

public class RequestUriParser {

    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final String QUERY_STRING_REGEX = "\\?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_SEPARATOR = "=";

    private static final RequestUriParser INSTANCE = new RequestUriParser();

    public static RequestUriParser getInstance() {
        return INSTANCE;
    }

    public String parsePath(final String rawRequestUri) {
        return rawRequestUri.split(QUERY_STRING_REGEX)[0];
    }

    public Map<String, String> parseQueryParameters(final String rawRequestUri) {
        if (!rawRequestUri.contains(QUERY_STRING_SEPARATOR)) {
            return new HashMap<>();
        }

        final String queryString = rawRequestUri.split(QUERY_STRING_REGEX)[1];
        final String[] queryParameters = queryString.split(QUERY_PARAMETER_SEPARATOR);
        final Map<String, String> parameters = new HashMap<>();
        for (String parameter : queryParameters) {
            final String[] keyAndValue = parameter.split(QUERY_PARAMETER_KEY_VALUE_SEPARATOR);
            final String key = keyAndValue[0];
            String value = "";
            if (keyAndValue.length == 2) {
                value = keyAndValue[1];
            }
            parameters.put(key, value);
        }

        return parameters;
    }

    private RequestUriParser() {
    }
}
