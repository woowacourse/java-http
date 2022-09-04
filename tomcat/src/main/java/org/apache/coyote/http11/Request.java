package org.apache.coyote.http11;

public class Request {

    private final Path path;
    private final QueryParameters queryParameters;

    private Request(Path path, QueryParameters queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public static Request of(StartLine startLine) {
        String uri = startLine.getUri();
        return new Request(Path.of(uri), QueryParameters.of(uri));
    }

    public Path getPath() {
        return path;
    }

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }
}
