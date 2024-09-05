package org.apache.coyote.request.uri;

public class URI {

    private final PathVariable pathVariable;

    private final QueryParams queryParams;

    public URI(String url) {
        String[] uriParts = url.split("\\?");
        this.pathVariable = new PathVariable(uriParts[0]);
        this.queryParams = createQueryParams(uriParts);
    }

    private QueryParams createQueryParams(String[] uriParts) {
        if (uriParts.length == 1) {
            return new QueryParams("");
        }
        return new QueryParams(uriParts[1]);
    }

    public String getPath() {
        return this.pathVariable.getPath();
    }

    public String getQueryParamValue(String key) {
        return this.queryParams.getValue(key);
    }
}
