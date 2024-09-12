package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryParameter {

    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final String QUERY_PARAMETER_REGEX = "\\?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int QUERY_PARAMETER_INDEX = 1;

    private final Map<String, String> parameters;

    public QueryParameter(String uri) {
        this.parameters = parseQueryParameter(uri);
    }

    private Map<String, String> parseQueryParameter(String uri) {
        if (uri == null || !uri.contains(QUERY_PARAMETER_DELIMITER)) {
            return Collections.emptyMap();
        }
        return toMap(uri.split(QUERY_PARAMETER_REGEX)[QUERY_PARAMETER_INDEX]);
    }

    private Map<String, String> toMap(String queryParameter) {
        Map<String, String> parameters = new LinkedHashMap<>();

        for (String parameter : queryParameter.split(PARAMETER_DELIMITER)) {
            String[] keyAndValue = parameter.split(KEY_VALUE_DELIMITER);
            parameters.put(decode(keyAndValue[KEY_INDEX].trim()), decode(keyAndValue[VALUE_INDEX].trim()));
        }

        return parameters;
    }

    private String decode(String source) {
        return URLDecoder.decode(source, StandardCharsets.UTF_8);
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    public String getAttribute(String name) {
        return parameters.get(name);
    }
}
