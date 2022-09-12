package web.request;

import java.util.Optional;

public class RequestUri {

    private static final String STATIC_RESOURCE_TYPE_DELIMITER = ".";
    private static final String QUERY_STRING_DELIMITER = "?";

    private String value;

    public RequestUri(final String value) {
        this.value = value;
    }

    public String parsePath() {
        return this.value.split("\\" + QUERY_STRING_DELIMITER)[0];
    }

    public Optional<String> findStaticResourceType() {
        if (!this.value.contains(STATIC_RESOURCE_TYPE_DELIMITER)) {
            return Optional.empty();
        }
        String[] path = this.value.split("\\" + STATIC_RESOURCE_TYPE_DELIMITER);
        return Optional.of(path[path.length - 1]);
    }

    public String getValue() {
        return value;
    }
}
