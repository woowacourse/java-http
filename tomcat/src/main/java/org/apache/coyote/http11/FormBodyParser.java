package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class FormBodyParser implements BodyParser {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String VALUE_DELIMITER = "=";

    @Override
    public Map<String, String> parse(final String body) {
        final Map<String, String> parameters = new HashMap<>();
        final String[] splitParameters = body.split(PARAMETER_DELIMITER);
        for (final String splitParameter : splitParameters) {
            final String[] parameter = splitParameter.split(VALUE_DELIMITER);
            final String key = parameter[0];
            final String value = parameter[1];
            parameters.put(key, value);
        }
        return parameters;
    }
}
