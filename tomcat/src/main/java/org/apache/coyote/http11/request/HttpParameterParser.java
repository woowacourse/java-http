package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpParameterParser {
    private HttpParameterParser() {

    }

    public static Map<String, String> parseParametersIntoMap(String parameters) {
        final Map<String, String> queryParameters = new HashMap<>();
        final StringTokenizer stringTokenizer = new StringTokenizer(parameters, "&");
        while (stringTokenizer.hasMoreTokens()) {
            final String parameter = stringTokenizer.nextToken();
            parseParameterIntoMap(queryParameters, parameter);
        }
        return queryParameters;
    }

    private static void parseParameterIntoMap(final Map<String, String> queryParameters, final String parameter) {
        final StringTokenizer stringTokenizer = new StringTokenizer(parameter, "=");
        if (!existsParameterValue(stringTokenizer)) {
            return;
        }
        queryParameters.put(stringTokenizer.nextToken(), stringTokenizer.nextToken());
    }

    private static boolean existsParameterValue(final StringTokenizer stringTokenizer) {
        return stringTokenizer.countTokens() == 2;
    }
}
