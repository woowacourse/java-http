package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class FormData {

    private final Map<String, String> params;

    private FormData(Map<String, String> params) {
        this.params = params;
    }

    public static FormData from(HttpBody httpBody) {
        String formData = httpBody.getHttpBody();

        if ("".equals(formData)) {
            return new FormData(Collections.emptyMap());
        }

        String[] params = formData.split("&");

        Map<String, String> paramMap = Arrays.stream(params)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));

        return new FormData(paramMap);
    }

    public String get(String key) {
        if (params.containsKey(key)) {
            return params.get(key);
        }

        return null;
    }

}
