package org.apache.coyote.http11.request.parser;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class WebFormUrlParser implements RequestBodyParser {

    private static final String WEB_FORM_DATA_DELIMITER = "&";
    private static final String WEB_FORM_DATA_KEY_VALUE_DELIMITER = "=";

    @Override
    public Map<String, String> parseParameters(String requestBody) {
        return Arrays.stream(requestBody.split(WEB_FORM_DATA_DELIMITER))
                .collect(Collectors.toUnmodifiableMap(
                        WebFormUrlParser::getKey,
                        WebFormUrlParser::getValue)
                );
    }

    private static String getValue(String str) {
        int delimiterIndex = str.lastIndexOf(WEB_FORM_DATA_KEY_VALUE_DELIMITER);
        return str.substring(delimiterIndex + 1);
    }

    private static String getKey(String str) {
        int delimiterIndex = str.lastIndexOf(WEB_FORM_DATA_KEY_VALUE_DELIMITER);
        return str.substring(0, delimiterIndex);
    }
}
