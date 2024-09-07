package org.apache.coyote.http11.request.bodyparser;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class WebFormUrlParser implements RequestBodyParser {

    private static final String WEB_FORM_DATA_DELIMITER = "&";
    private static final String WEB_FORM_DATA_KEY_VALUE_DELIMITER = "=";

    @Override
    public Map<String, String> parseParameters(String requestBody) {
        return Arrays.stream(requestBody.split(WEB_FORM_DATA_DELIMITER))
                .map(str -> str.split(WEB_FORM_DATA_KEY_VALUE_DELIMITER))
                .collect(
                        Collectors.toUnmodifiableMap(split -> split[0], split -> split[1])
                );
    }
}
