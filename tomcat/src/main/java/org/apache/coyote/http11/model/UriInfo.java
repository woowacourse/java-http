package org.apache.coyote.http11.model;

public record UriInfo(
        String path,
    String queryString
) {

    public static UriInfo makeUriInfo(String url) {
        int queryStartIndex = url.indexOf("?");

        if (queryStartIndex == -1) {
            return new UriInfo(url, "");
        }

        String path = url.substring(0, queryStartIndex);
        String queryString = url.substring(queryStartIndex + 1);
        return new UriInfo(path, queryString);
    }
}
