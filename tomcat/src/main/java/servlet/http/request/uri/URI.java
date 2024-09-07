package servlet.http.request.uri;

public class URI {

    private static final int EMPTY_QUERY_PARAMS = 1;

    private final Path path;

    private final QueryParams queryParams;

    public URI(String uri) {
        String[] uriParts = uri.split("\\?");
        this.path = new Path(uriParts[0]);
        this.queryParams = createQueryParams(uriParts);
    }

    private QueryParams createQueryParams(String[] uriParts) {
        if (uriParts.length == EMPTY_QUERY_PARAMS) {
            return QueryParams.EMPTY;
        }
        return QueryParams.from(uriParts[1]);
    }

    public String getPath() {
        return this.path.getPath();
    }

    public String getQueryParamValue(String key) {
        return this.queryParams.getValue(key);
    }

    public boolean existQueryParams() {
        return this.queryParams.existQueryParams();
    }
}
