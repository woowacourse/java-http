package org.apache.coyote.http11;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Path {

    private static final String STATIC_FILE_PREFIX = "static";
    private static final String STATIC_FILE_SUFFIX = ".html";

    private final String requestPath;
    private final URL absolutePath;
    private final Map<String, String> parameters;

    public Path(final String target) {
        if (target.contains("?")) {
            this.requestPath = target.substring(0, target.indexOf("?"));
            this.absolutePath = getClass().getClassLoader()
                    .getResource(STATIC_FILE_PREFIX + target.substring(0, target.indexOf("?")) + STATIC_FILE_SUFFIX);
            this.parameters = parseQueryParam(target.substring(target.indexOf('?') + 1));
            return;
        }
        if (target.contains(".")) {
            this.requestPath = target;
            this.absolutePath = getClass().getClassLoader().getResource(STATIC_FILE_PREFIX + target);
            this.parameters = Map.of();
            return;
        }
        this.requestPath = target;
        this.absolutePath = getClass().getClassLoader().getResource(STATIC_FILE_PREFIX + target + STATIC_FILE_SUFFIX);
        this.parameters = Map.of();
    }

    private static Map<String, String> parseQueryParam(final String query) {
        final var result = new HashMap<String, String>();
        final var queryParams = query.split("&");
        for (final var param : queryParams) {
            final var keyValue = param.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public URL getAbsolutePath() {
        return absolutePath;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public String toString() {
        return "Path{" +
               "requestPath='" + requestPath + '\'' +
               ", absolutePath=" + absolutePath +
               ", parameters=" + parameters +
               '}';
    }
}
