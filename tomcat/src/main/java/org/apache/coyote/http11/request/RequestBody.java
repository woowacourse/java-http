package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestBody {

    private static final int BODY_FIELD_INDEX = 0;
    private static final int BODY_VALUE_INDEX = 1;
    private static final String PARAMETER_DELIMITER = "&";
    private static final String FIELD_AND_VALUE_DELIMITER = "=";

    private final Map<String, String> parametersMap;

    public RequestBody(Map<String, String> parametersMap) {
        this.parametersMap = parametersMap;
    }

    public static RequestBody from(String requestBody) {
        Map<String, String> parametersMap = Arrays.stream(requestBody.split(PARAMETER_DELIMITER))
                .map(parameter -> parameter.split(FIELD_AND_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        fieldAndValue -> fieldAndValue[BODY_FIELD_INDEX].trim(),
                        fieldAndValue -> fieldAndValue[BODY_VALUE_INDEX].trim()
                ));
        return new RequestBody(parametersMap);
    }

    public String get(String key) {
        return parametersMap.get(key);
    }

    public Map<String, String> parametersMap() {
        return new HashMap<>(parametersMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestBody that = (RequestBody) o;
        return Objects.equals(parametersMap, that.parametersMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parametersMap);
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "parametersMap=" + parametersMap +
                '}';
    }
}
