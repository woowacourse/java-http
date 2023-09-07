package org.apache.coyote.http11.parser;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormDataParser {

    private static final String FORM_DELIMITER = "&";
    private static final String FORM_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE = 0;
    private static final int VALUE_INDEX = 1;

    private FormDataParser() {
    }

    public static Map<String, String> parse(final String formData) {
        final String decodedFormData = URLDecoder.decode(formData, UTF_8);

        final String[] params = decodedFormData.split(FORM_DELIMITER);

        return Arrays.stream(params).map(param -> param.split(FORM_VALUE_DELIMITER))
            .filter(keyValue -> keyValue.length == 2)
            .collect(Collectors.toMap(
                keyValue -> keyValue[KEY_VALUE],
                keyValue -> keyValue[VALUE_INDEX],
                (prev, update) -> update
            ));
    }
}
