package org.apache.coyote.http11.request;

public class HttpRequest {

    private final String content;

    public HttpRequest(String content) {//TODO byte[]로 받기
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
