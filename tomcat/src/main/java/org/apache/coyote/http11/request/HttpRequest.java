package org.apache.coyote.http11.request;

public class HttpRequest {

    private final String content;

    public HttpRequest(String content) {//TODO byte[]로 받기
        this.content = content;
    }

    public String getTarget() { //TODO 따로 빼야하나?
        String[] startLine = getStartLine().split(" ");
        return startLine[1];
    }

    public String getPath() {
        String target = getTarget();
        int i = target.lastIndexOf('?');
        if (i == -1) {
            return target; // login.html, login // login.css
        }
        return target.substring(0, i);
    }

    private String getStartLine() {
        return content.split("\n")[0];
    }
}
