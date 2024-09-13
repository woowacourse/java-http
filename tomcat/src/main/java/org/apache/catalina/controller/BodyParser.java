package org.apache.catalina.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BodyParser {

    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String EMPTY_STRING = "";
    private static final int PARAM_VALUE_INDEX = 1;

    public static Map<String, String> parse(String body) {
        List<String> params = Arrays.asList(body.split(PARAM_DELIMITER));
        return params.stream()
                .map(param -> Arrays.asList(param.split(KEY_VALUE_DELIMITER)))
                .collect(Collectors.toMap(List::getFirst, BodyParser::getString));
    }

    private static String getString(List<String> param) {
        if (param.size() > PARAM_VALUE_INDEX) {
            return param.get(PARAM_VALUE_INDEX);
        }
        return EMPTY_STRING;
    }
}
