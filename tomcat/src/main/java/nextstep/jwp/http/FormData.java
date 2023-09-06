package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class FormData {

    private static final String FORM_DATA_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> params;

    private FormData(Map<String, String> params) {
        this.params = params;
    }

    public static FormData from(HttpBody httpBody) {
        String formData = httpBody.getHttpBody();

        if (formData == null || formData.isBlank()) {
            return new FormData(Collections.emptyMap());
        }

        String[] params = formData.split(FORM_DATA_DELIMITER);

        Map<String, String> paramMap = Arrays.stream(params)
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .filter(param -> param.length == 2)
                .collect(Collectors.toMap(param -> param[KEY_INDEX], param -> param[VALUE_INDEX]));

        return new FormData(paramMap);
    }

    public String get(String key) {
        if (params.containsKey(key)) {
            return params.get(key);
        }

        return null;
    }

}
