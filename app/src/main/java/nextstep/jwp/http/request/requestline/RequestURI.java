package nextstep.jwp.http.request.requestline;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.util.ParamExtractor;

public class RequestURI {

    private final String requestURI;
    private final Map<String, String> parameters;

    public RequestURI(String requestURI) {
        this.requestURI = extractURI(requestURI);
        this.parameters = extractParams(requestURI);
    }

    private String extractURI(String uri) {
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    private Map<String, String> extractParams(String uri) {
        if (!uri.contains("?")) {
            return new HashMap<>();
        }
        return ParamExtractor.extractParams(uri);
    }

    public String getParamValue(String key) {
        return parameters.get(key);
    }

    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestURI that = (RequestURI) o;
        return Objects.equals(requestURI, that.requestURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestURI);
    }
}
