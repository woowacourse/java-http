package org.apache.coyote.component;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Path {

    private static final String STATIC_FILE_PREFIX = "static";
    private static final String STATIC_FILE_SUFFIX = ".html";
    private static final String QUERY_PREFIX = "?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String EXTENSION_PREFIX = ".";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String requestPath;
    private final URL absolutePath;
    private final Map<String, String> parameters;

    private Path(final String requestPath, final URL absolutePath, final Map<String, String> parameters) {
        this.requestPath = requestPath;
        this.absolutePath = absolutePath;
        this.parameters = parameters;
    }

    private Path(final String requestPath, final URL absolutePath) {
        this.requestPath = requestPath;
        this.absolutePath = absolutePath;
        this.parameters = Collections.emptyMap();
    }

    public static Path from(final String requestPath) {
        if (requestPath.contains(QUERY_PREFIX)) {
            return parsePathWithQuery(requestPath);
        }
        if (requestPath.contains(EXTENSION_PREFIX)) {
            final var absolutePath = Path.class.getClassLoader().getResource(STATIC_FILE_PREFIX + requestPath);
            return new Path(requestPath, absolutePath);
        }
        final var absolutePath = Path.class.getClassLoader()
                .getResource(STATIC_FILE_PREFIX + requestPath + STATIC_FILE_SUFFIX);
        return new Path(requestPath, absolutePath);
    }

    private static Path parsePathWithQuery(final String target) {
        final var requestPath = target.substring(0, target.indexOf(QUERY_PREFIX));
        final var absolutePath = Path.class.getClassLoader()
                .getResource(STATIC_FILE_PREFIX + target.substring(0, target.indexOf(QUERY_PREFIX))
                             + STATIC_FILE_SUFFIX);
        final var parameters = parseQueryParam(target.substring(target.indexOf(QUERY_PREFIX) + 1));
        return new Path(requestPath, absolutePath, parameters);
    }

    private static Map<String, String> parseQueryParam(final String query) {
        final var result = new HashMap<String, String>();
        final var queryParams = query.split(QUERY_PARAMETER_SEPARATOR);
        for (final var param : queryParams) {
            final var key = param.split(KEY_VALUE_SEPARATOR)[KEY_INDEX];
            final var value = param.split(KEY_VALUE_SEPARATOR)[VALUE_INDEX];
            result.put(key, value);
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
