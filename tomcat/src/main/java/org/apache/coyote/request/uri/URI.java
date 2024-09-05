package org.apache.coyote.request.uri;

public class URI {

    private static final int EMPTY_QUERY_PARAMS = 1;

    private final PathVariable pathVariable;

    private final QueryParams queryParams;

    public URI(String uri) {
        String[] uriParts = uri.split("\\?");
        this.pathVariable = new PathVariable(uriParts[0]);
        this.queryParams = createQueryParams(uriParts);
    }

    private QueryParams createQueryParams(String[] uriParts) {
        if (uriParts.length == EMPTY_QUERY_PARAMS) {
            return QueryParams.EMPTY;
        }
        return QueryParams.from(uriParts[1]);
    }

    public String getPath() {
        return this.pathVariable.getPath();
    }

    public String getQueryParamValue(String key) {
        return this.queryParams.getValue(key);
    }

    public boolean existQueryParams() {
        return this.queryParams.existQueryParams();
    }
}
