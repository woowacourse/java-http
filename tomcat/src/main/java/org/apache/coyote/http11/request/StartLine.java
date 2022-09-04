package org.apache.coyote.http11.request;

public class StartLine {

    private final String httpMethod;
    private final String uri;
    private final String httpVersion;

    public StartLine(String startLine) {
        String[] splitStartLine = startLine.split(" ");
        this.httpMethod = splitStartLine[0];
        this.uri = splitStartLine[1];
        this.httpVersion = splitStartLine[2];
    }

    public String getUri() {
        return this.uri;
    }
}
