package nextstep.jwp.http.request.requestline;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.http.util.ParamExtractor;

public class RequestPath {

    private static final String QUERY_STRING_DENOTE_PREFIX = "?";

    private final String path;
    private final Map<String, String> parameters;

    public RequestPath(String path) {
        this.path = extractURI(path);
        this.parameters = extractParams(path);
    }

    private String extractURI(String uri) {
        if (uri.contains(QUERY_STRING_DENOTE_PREFIX)) {
            return uri.substring(0, uri.indexOf(QUERY_STRING_DENOTE_PREFIX));
        }
        return uri;
    }

    private Map<String, String> extractParams(String uri) {
        if (!uri.contains(QUERY_STRING_DENOTE_PREFIX)) {
            return new HashMap<>();
        }
        return ParamExtractor.extractParams(uri);
    }

    public boolean containsExtension(String extension) {
        return path.contains(extension);
    }

    public String getParamValue(String key) {
        return parameters.get(key);
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestPath that = (RequestPath) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
