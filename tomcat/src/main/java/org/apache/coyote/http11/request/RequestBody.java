package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.utils.Separator;

public class RequestBody {

    private static final String PARAMETER_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private Map<String, String> parameters;

    public static RequestBody create(String body) {
        if (body.isBlank()) {
            return new RequestBody(Map.of());
        }
        
        List<String> parameterEntries = List.of(body.split(PARAMETER_SEPARATOR));
        Map<String, String> parameters = Separator.separateKeyValueBy(parameterEntries, KEY_VALUE_SEPARATOR);

        return new RequestBody(parameters);
    }

    private RequestBody(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
