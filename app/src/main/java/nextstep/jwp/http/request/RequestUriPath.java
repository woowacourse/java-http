package nextstep.jwp.http.request;

import java.util.Objects;

/**
 * /login?something1=123&something2=123
 */

public class RequestUriPath {
    private final SourcePath sourcePath;
    private final QueryParams queryParams;

    private RequestUriPath(SourcePath sourcePath, QueryParams queryParams) {
        this.sourcePath = sourcePath;
        this.queryParams = queryParams;
    }

    public static RequestUriPath of(String uriPath) {
        SourcePath sourcePath = SourcePath.of(uriPath);
        String queryString = queryString(uriPath);
        return new RequestUriPath(sourcePath, QueryParams.of(queryString));
    }

    private static String queryString(String uri) {
        int index = uri.indexOf("?");
        if (index > 0) {
            return uri.substring(index + 1);
        }
        return "";
    }

    public SourcePath getSourcePath() {
        return sourcePath;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    public boolean isPath(String path) {
        return sourcePath.isPath(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestUriPath that = (RequestUriPath) o;
        return Objects.equals(sourcePath, that.sourcePath) && Objects.equals(queryParams, that.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourcePath, queryParams);
    }
}
