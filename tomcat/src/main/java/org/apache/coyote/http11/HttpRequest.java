package org.apache.coyote.http11;

public class HttpRequest {

    private final String content;

    public HttpRequest(String content) {
        this.content = content;
    }

    public String getTarget() {
        String[] startLine = getStartLine().split(" ");
        return startLine[1];
    }

    private String getStartLine() {
        return content.split("\n")[0];
    }
}
