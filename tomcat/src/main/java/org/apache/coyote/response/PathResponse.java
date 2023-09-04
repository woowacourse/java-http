package org.apache.coyote.response;

public class PathResponse implements Response{
    private static final String FILE_TYPE_FIXTURE = "html";
    private static final String FILE_EXTENSION_DELIMITER = ".";


    private final String path;
    private final String fileType;
    private final int statusCode;
    private final String statusValue;

    public PathResponse(final String path, int statusCode, String statusValue) {
        this.path = path + FILE_EXTENSION_DELIMITER + FILE_TYPE_FIXTURE;
        this.fileType = FILE_TYPE_FIXTURE;
        this.statusCode = statusCode;
        this.statusValue = statusValue;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getFileType() {
        return this.fileType;
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
