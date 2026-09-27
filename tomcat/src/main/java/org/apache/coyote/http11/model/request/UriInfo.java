package org.apache.coyote.http11.model.request;

public record UriInfo(
        String path,
        QueryParameters queryParameters
) {

    public static UriInfo makeUriInfo(String url) {
        int queryStartIndex = url.indexOf("?");

        if (queryStartIndex == -1) {
            return new UriInfo(url, QueryParameters.empty());
        }

        String path = url.substring(0, queryStartIndex);
        String queryString = url.substring(queryStartIndex + 1);
        return new UriInfo(path, QueryParameters.from(queryString));
    }

    public boolean hasQueryParameters() {
        return !queryParameters.isEmpty();
    }
}
