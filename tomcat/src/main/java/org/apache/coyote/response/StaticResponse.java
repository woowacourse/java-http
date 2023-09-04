package org.apache.coyote.response;

public class StaticResponse implements Response {
    private final String fileType;
    private final String path;

    public StaticResponse(final String fileType, final String path) {
        this.fileType = fileType;
        this.path = path;
    }


    @Override
    public String getFileType() {
        return fileType;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getBodyString() {
        return "";
    }
}
