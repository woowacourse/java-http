package org.apache.coyote.http11.httprequest;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> parameters;

    public static RequestBody from(final String rawRequestBody) {
        final String[] parameters = rawRequestBody.split(QUERY_PARAMETER_SEPARATOR);
        final Map<String, String> bodyParameters = new HashMap<>();
        for (String parameter : parameters) {
            final String[] keyAndValue = parameter.split(QUERY_PARAMETER_KEY_VALUE_SEPARATOR);
            final String key = keyAndValue[0];
            String value = "";
            if (keyAndValue.length == 2) {
                value = keyAndValue[1];
            }
            bodyParameters.put(key, value);
        }

        return new RequestBody(bodyParameters);
    }

    public static RequestBody createEmptyBody() {
        return new RequestBody(new HashMap<>());
    }

    public String getParameter(final String key) {
        return parameters.get(key);
    }

    private RequestBody(final Map<String, String> parameters) {
        this.parameters = new HashMap<>(parameters);
    }
}
