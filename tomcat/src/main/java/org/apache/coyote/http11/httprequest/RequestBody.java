package org.apache.coyote.http11.httprequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> parameters;

    public static RequestBody from(final String rawRequestBody) {
        final String[] parameters = rawRequestBody.split(QUERY_PARAMETER_SEPARATOR);
        final Map<String, String> bodyParameters = Arrays.stream(parameters)
                .map(param -> param.split(QUERY_PARAMETER_KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1],
                        (oldValue, newValue) -> newValue
                ));

        return new RequestBody(bodyParameters);
    }

    public static RequestBody createEmptyBody() {
        return new RequestBody(new HashMap<>());
    }

    public String getParameter(final String key) {
        return parameters.get(key);
    }

    private RequestBody(final Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
