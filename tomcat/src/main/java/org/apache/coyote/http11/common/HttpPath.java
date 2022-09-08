package org.apache.coyote.http11.common;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.util.ParamParser;

public class HttpPath {

    private static final String PARAMS_DELIMITER = "&";

    private final String value;
    private final Map<String, String> params;

    private HttpPath(final String value, final Map<String, String> params) {
        this.value = value;
        this.params = new HashMap<>(params);
    }

    public static HttpPath from(final String rawUri) {
        final URI uri = URI.create(rawUri);
        final String path = uri.getPath();
        final String query = uri.getQuery();

        final Map<String, String> params = ParamParser.parseOf(query, PARAMS_DELIMITER);

        return new HttpPath(path, params);
    }

    public String getValue() {
        return value;
    }

    public String getParam(final String key) {
        return params.get(key);
    }
}
