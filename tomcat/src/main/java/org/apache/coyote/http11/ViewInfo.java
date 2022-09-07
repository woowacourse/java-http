package org.apache.coyote.http11;

public class ViewInfo {

    private final String viewContent;
    private final String contentType;
    private final int contentLength;

    public ViewInfo(final String viewContent, final String contentType, final int contentLength) {
        this.viewContent = viewContent;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public String getViewContent() {
        return viewContent;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }
}
