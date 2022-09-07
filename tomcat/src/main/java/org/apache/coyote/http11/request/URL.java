package org.apache.coyote.http11.request;

public class URL {

    private final Path path;
    private final QueryParameters queryParameters;

    private URL(Path path, QueryParameters queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public static URL of(String url) {
        return new URL(Path.of(url), QueryParameters.of(url));
    }

    public Path getPath() {
        return this.path;
    }

    public QueryParameters getQueryParameters() {
        return this.queryParameters;
    }
}
