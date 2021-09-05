package nextstep.jwp.framework.http.common;

import java.util.Map;

public class HttpBody {

    private final QueryParams queryParams;

    public HttpBody() {
        this.queryParams = new QueryParams();
    }

    public HttpBody(final String queryParams) {
        this(new QueryParams(queryParams));
    }

    public HttpBody(final QueryParams queryParams) {
        this.queryParams = queryParams;
    }

    public boolean hasBody() {
        return queryParams.count() != 0;
    }

    public boolean hasNotBody() {
        return !hasBody();
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getValue();
    }

    @Override
    public String toString() {
        return queryParams.toString();
    }
}
