package org.apache.coyote.http11.request;

public class StartLine {

    private final HttpMethod httpMethod;
    private final URL url;
    private final String httpVersion;

    public StartLine(String startLine) {
        String[] splitStartLine = startLine.split(" ");
        this.httpMethod = HttpMethod.findHttpMethod(splitStartLine[0]);
        this.url = URL.of(splitStartLine[1]);
        this.httpVersion = splitStartLine[2];
    }

    public boolean checkRequest(String path) {
        return this.url.getPath().checkRequest(path);
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public Path getPath() {
        return this.url.getPath();
    }

    public QueryParameters getQueryParameters() {
        return this.url.getQueryParameters();
    }
}
