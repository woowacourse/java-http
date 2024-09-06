package org.apache.coyote.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String FORM_DATA_DELIMITER = "&";
    private static final String FORM_FIELD_DELIMITER = "=";
    private static final int SPLIT_LIMIT = 2;

    private final Map<String, String> values;

    private RequestBody(Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody empty() {
        return new RequestBody(Map.of());
    }

    public static RequestBody fromFormData(String formData) {
        String[] formFields = formData.split(FORM_DATA_DELIMITER);
        Map<String, String> bodies = parseFields(formFields, FORM_FIELD_DELIMITER);
        return new RequestBody(bodies);
    }

    private static Map<String, String> parseFields(String[] bodies, String delimiter) {
        return Arrays.stream(bodies)
                .map(param -> param.split(delimiter, SPLIT_LIMIT))
                .filter(entry -> entry.length == SPLIT_LIMIT)
                .collect(HashMap::new,
                         (map, entry) -> map.put(entry[0], entry[1]),
                         HashMap::putAll);
    }

    public String getValue(String key) {
        return values.get(key);
    }

    @Override
    public String toString() {
        return "RequestBody{" +
               "values=" + values +
               '}';
    }
}
