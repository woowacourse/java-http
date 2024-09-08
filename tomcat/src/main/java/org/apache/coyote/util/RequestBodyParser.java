package org.apache.coyote.util;

import java.util.Arrays;
import java.util.Map;
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
                            query -> query[KEY_INDEX],
                            query -> query[VALUE_INDEX]
                    ));
        }
        return Map.of();
    }
}
