package org.apache.coyote.http.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class HttpParameters {

    private static final String PARAMETERS_DELIMITER = "&";
    private static final String PARAMETER_DELIMITER = "=";

    private final Map<String, String> parameters;

    public HttpParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static HttpParameters from(String parameterString) {
        return new HttpParameters(Arrays.stream(parameterString.split(PARAMETERS_DELIMITER))
                                        .map(parameter -> parameter.split(PARAMETER_DELIMITER))
                                        .filter(keyValue -> keyValue.length == 2)
                                        .collect(toMap(keyValue -> keyValue[0], keyValue -> keyValue[1])));
    }

    public void addAll(HttpParameters toCombine) {
        parameters.putAll(toCombine.getParameters());
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }
}
