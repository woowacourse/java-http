package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Path {

    private final String path;
    private final Map<String, String> parameters;

    public Path(String path) {
        validatePath(path);
        this.path = findPath(path);
        this.parameters = findParameters(path);
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
            List<String> queryParameters = List.of(path.substring(path.indexOf("?")+1).split("&"));
            return queryParameters.stream()
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(q -> q[0], q -> q[1]));
        }
        return new HashMap<>();
    }

    public boolean hasQueryParameter() {
        return parameters != null && !parameters.isEmpty();
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
