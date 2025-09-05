package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final String method;
    private final String uri;
    private final String queryString;
    private final Map<String, String> parameters;

    public HttpRequest(final String method, final String uri, final String queryString) {
        this.method = method;
        this.uri = uri;
        this.queryString = queryString;
        this.parameters = parseParameters(queryString);
    }

    private Map<String, String> parseParameters(final String queryString) {
        final Map<String, String> params = new HashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (final Exception e) {
                    log.warn("Failed to parse query string: {}", queryString, e);
                }
            }
        }
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }
}
