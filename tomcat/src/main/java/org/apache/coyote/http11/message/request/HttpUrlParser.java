package org.apache.coyote.http11.message.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpUrlParser {

    private static final String URL_DELIMITER = "\\?";
    private static final String QUERY_PARAMETERS_DELIMITER = "&";
    private static final String QUERY_PARAMETER_DELIMITER = "=";
    private static final String QUERY_PARAMETER_VALUES_DELIMITER = ",";
    private static final int PATH_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final int QUERY_PARAMETER_KEY_INDEX = 0;
    private static final int QUERY_PARAMETER_VALUE_INDEX = 1;

    public static HttpUrl parseUrl(String url) {
        String[] urlElements = url.split(URL_DELIMITER);
        String path = urlElements[PATH_INDEX];

        if (urlElements.length == 1) {
            return new HttpUrl(path, new QueryParameters(new HashMap<>()));
        }

        String queryString = urlElements[QUERY_STRING_INDEX];

        Map<String, List<String>> queryParameters = Arrays.stream(queryString.split(QUERY_PARAMETERS_DELIMITER))
                .map(HttpUrlParser::parseQueryParameter)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, HttpUrlParser::mergeValues));

        return new HttpUrl(path, new QueryParameters(queryParameters));
    }

    private static Map.Entry<String, List<String>> parseQueryParameter(String queryParameter) {
        String[] queryParameterElements = queryParameter.split(QUERY_PARAMETER_DELIMITER);
        String key = queryParameterElements[QUERY_PARAMETER_KEY_INDEX];
        List<String> values = Arrays.asList(
                queryParameterElements[QUERY_PARAMETER_VALUE_INDEX].split(QUERY_PARAMETER_VALUES_DELIMITER));

        return Map.entry(key, values);
    }

    private static List<String> mergeValues(List<String> existingValues, List<String> newValues) {
        existingValues.addAll(newValues);
        return existingValues;
    }
}
