package org.apache.coyote.response;

public class PathResponse implements Response{
    private static final String FILE_TYPE_FIXTURE = "html";
    private static final String FILE_EXTENSION_DELIMITER = ".";


    private final String path;
    private final String fileType;

    public PathResponse(final String path) {
        this.path = path + FILE_EXTENSION_DELIMITER + FILE_TYPE_FIXTURE;
        this.fileType = FILE_TYPE_FIXTURE;
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
}
