package org.apache.coyote.response;

public class StaticResponse implements Response {
    private final String fileType;
    private final String path;
    private final int statusCode;
    private final String statusValue;

    public StaticResponse(final String fileType, final String path, int statusCode, String statusValue) {
        this.fileType = fileType;
        this.path = path;
        this.statusCode = statusCode;
        this.statusValue = statusValue;
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

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusValue() {
        return statusValue;
    }
}
