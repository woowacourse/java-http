package org.apache.coyote.http11.util;

import java.util.HashMap;
import java.util.Map;

public class ParamParser {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String EMPTY_STRING = "";

    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final int NO_VALUE_LENGTH = 1;

    private ParamParser() {
    }

    public static Map<String, String> parseOf(final String rawParams, final String delimiter) {
        if (rawParams.isBlank()) {
            return new HashMap<>();
        }

        final Map<String, String> parsedParams = new HashMap<>();

        final String[] params = rawParams.split(delimiter);
        for (final String param : params) {
            parseParam(parsedParams, param);
        }

        return parsedParams;
    }

    private static void parseParam(final Map<String, String> parsedParams, final String param) {
        final String stripedParam = param.strip();
        final String[] paramKeyValue = stripedParam.split(KEY_VALUE_DELIMITER);

        if (hasNoValue(paramKeyValue)) {
            parsedParams.put(paramKeyValue[KEY], EMPTY_STRING);
            return;
        }
        parsedParams.put(paramKeyValue[KEY], paramKeyValue[VALUE]);
    }

    private static boolean hasNoValue(final String[] paramKeyValue) {
        return paramKeyValue.length == NO_VALUE_LENGTH;
    }
}
