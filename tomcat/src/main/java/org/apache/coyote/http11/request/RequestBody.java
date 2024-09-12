package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestBody {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    public RequestBody(String body) {
        this.body = parseBody(body);
    }

    private Map<String, String> parseBody(String body) {
        if (body == null || body.isBlank()) {
            return Collections.emptyMap();
        }
        return toMap(body);
    }

    private Map<String, String> toMap(String body) {
        Map<String, String> parameters = new LinkedHashMap<>();

        for (String parameter : body.split(PARAMETER_DELIMITER)) {
            String[] keyAndValue = parameter.split(KEY_VALUE_DELIMITER);
            parameters.put(decode(keyAndValue[KEY_INDEX].trim()), decode(keyAndValue[VALUE_INDEX].trim()));
        }

        return parameters;
    }

    private String decode(String source) {
        return URLDecoder.decode(source, StandardCharsets.UTF_8);
    }

    public String getAttribute(String name) {
        return body.get(name);
    }

    public String getValue() {
        return null;
    }
}
