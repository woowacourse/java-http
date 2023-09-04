package org.apache.coyote.response;

public class StringResponse implements Response{

    private final String bodyString;
    private final String fileType;
    private final int statusCode;
    private final String statusValue;

    public StringResponse(String bodyString, int statusCode, String statusValue) {
        this.bodyString = bodyString;
        this.fileType = "html";
        this.statusCode = statusCode;
        this.statusValue = statusValue;
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

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusValue() {
        return statusValue;
    }
}
