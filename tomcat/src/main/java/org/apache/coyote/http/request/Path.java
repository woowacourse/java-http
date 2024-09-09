package org.apache.coyote.http.request;

import org.apache.coyote.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Path {

    private final String uri;
    private final Map<String, String> parameters;

    public Path(String uri) {
        validatePath(uri);
        this.uri = findPath(uri);
        this.parameters = findParameters(uri);
    }

    private void validatePath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with '/'");
        }
    }

    private String findPath(String path) {
        if (path.contains("?")) {
            return path.substring(0, path.indexOf("?"));
        }
        return path;
    }

    private Map<String, String> findParameters(String path) {
        if (path.contains("?")) {
            return StringUtils.separateKeyValue(path.substring(path.indexOf("?") + 1));
        }
        return new HashMap<>();
    }

    public boolean isResourceUri() {
        return uri.contains(".");
    }

    public boolean hasQueryParameter() {
        return parameters != null && !parameters.isEmpty();
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
