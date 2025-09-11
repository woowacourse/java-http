package org.apache.coyote.http11.request.body;

import java.util.HashMap;
import java.util.Map;

public class RequestBodyParser {

    private static final RequestBodyParser INSTANCE = new RequestBodyParser();
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_SEPARATOR = "=";

    public static RequestBodyParser getInstance() {
        return INSTANCE;
    }

    public Map<String, String> parseBodyParameters(final String rawRequestBody) {
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
        return bodyParameters;
    }

    private RequestBodyParser() {
    }
}
