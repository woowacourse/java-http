package org.apache.coyote.http11;

public class ResponseBodyManager {

    private final String contents;

    public ResponseBodyManager(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public int getContentLength() {
        return contents.length();
    }
}
