package nextstep.jwp.framework;

import java.util.HashMap;
import java.util.Map;

public class RequestURIWithQuery {

    private static final String QUERY_DELIMITER = "\\?";
    private static final int URL_SPLIT_INDEX = 0;
    private static final int URL_START_INDEX = 1;

    private final String uri;
    private final Map<String, String> queryParams;

    public RequestURIWithQuery(final String uri) {
        this(uri, new HashMap<>());
    }

    public RequestURIWithQuery(final String uri, final Map<String, String> queryParams) {
        this.uri = uri;
        this.queryParams = queryParams;
    }

    public String url() {
        return uri.split(QUERY_DELIMITER)[URL_SPLIT_INDEX].substring(URL_START_INDEX);
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
