package org.apache.coyote.http11;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Path {

    private static final String STATIC_FILE_PREFIX = "static";
    private static final String STATIC_FILE_SUFFIX = ".html";

    private final String requestPath;
    private final URL absolutePath;
    private final Map<String, String> parameters;

    public Path(final String target) {
        if (target.contains("?")) { //TODO 객체 분리?
            this.requestPath = target.substring(0, target.indexOf("?"));
            this.absolutePath = getClass().getClassLoader()
                    .getResource(STATIC_FILE_PREFIX + target.substring(0, target.indexOf("?")) + STATIC_FILE_SUFFIX);
            this.parameters = new HashMap<>();
            final var query = target.substring(target.indexOf('?') + 1);
            final var queryParams = query.split("&");
            for (final var param : queryParams) {
                final var pair = param.split("=");
                this.parameters.put(pair[0], pair[1]);
            }
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

    public boolean isEqualPath(final String target) {
        return Objects.equals(requestPath, target);
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
               "value=" + requestPath +
               ", queryString=" + parameters +
               '}';
    }
}
