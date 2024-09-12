package org.apache.coyote.util;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import org.apache.coyote.request.HttpRequestBody;

public class RequestBodyParser {

    private static final String FORM_DATA_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private RequestBodyParser() {
    }

    public static Map<String, String> parseFormData(HttpRequestBody body) {
        if (body.hasBody()) {
            return Arrays.stream(body.value().split(FORM_DATA_DELIMITER))
                    .map(data -> data.split(KEY_VALUE_DELIMITER))
                    .collect(Collectors.toMap(
                            RequestBodyParser::parseKey,
                            RequestBodyParser::parseValue,
                            mergeOfFormDataDuplicateParameter()
                    ));
        }
        return Map.of();
    }

    private static String parseKey(String[] queryParameter) {
        return queryParameter[KEY_INDEX];
    }

    private static String parseValue(String[] queryParameter) {
        if (queryParameter.length == 1) {
            return "";
        }
        return queryParameter[VALUE_INDEX];
    }

    private static BinaryOperator<String> mergeOfFormDataDuplicateParameter() {
        return (existsValue, newValue) -> newValue;
    }
}
