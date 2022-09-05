package org.apache.coyote.common.request.parser.bodyparser;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultipartFormDataBodyParser implements Function<String, Map<String, String>> {

    private static final String FORM_DATA_ELEMENT_DELIMITER = "&";
    private static final String FORM_DATA_KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    MultipartFormDataBodyParser() {}

    @Override
    public Map<String, String> apply(final String multipartFormData) {
        final String[] formDataElements = multipartFormData.split(FORM_DATA_ELEMENT_DELIMITER);
        return Arrays.stream(formDataElements)
                .map(formData -> formData.split(FORM_DATA_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(formData -> formData[KEY_INDEX], formData -> formData[VALUE_INDEX]));
    }
}
