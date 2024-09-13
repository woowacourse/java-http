package org.apache.coyote.http11.message.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormParameters {

    private final Map<String, List<String>> parameters;

    public FormParameters(Map<String, List<String>> parameters) {
        this.parameters = new HashMap<>(parameters);
    }

    public FormParameters() {
        this(new HashMap<>());
    }

    public String getSingleValueByKey(String key) {
        if (!parameters.containsKey(key)) {
            throw new IllegalArgumentException(String.format("%s에 해당되는 form 파라미터가 존재하지 않습니다.", key));
        }

        return parameters.get(key).getFirst();
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }
}