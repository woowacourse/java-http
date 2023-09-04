package org.apache.coyote.response;

public class StringResponse implements Response{

    private final String bodyString;
    private final String fileType;

    public StringResponse(String bodyString) {
        this.bodyString = bodyString;
        this.fileType = "html";
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public String getFileType() {
        return this.fileType;
    }

    @Override
    public String getBodyString() {
        return this.bodyString;
    }
}
