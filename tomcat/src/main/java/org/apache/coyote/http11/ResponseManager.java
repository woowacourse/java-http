package org.apache.coyote.http11;

public class ResponseManager {

    private final ResponseHeaderManager responseHeaderManager;
    private final ResponseBodyManager responseBodyManager;

    public ResponseManager(ResponseHeaderManager responseHeaderManager, ResponseBodyManager responseBodyManager) {
        this.responseHeaderManager = responseHeaderManager;
        this.responseBodyManager = responseBodyManager;
    }

    public String getContents() {
        String contents = responseHeaderManager.buildHeader(responseBodyManager.getContentLength());
        contents += "\r\n";
        contents += responseBodyManager.getContents();
        return contents;
    }
}
