package org.apache.coyote.http11.response;

public class ResponseBody {

    private final String contentType;
    private final String value;

    public ResponseBody(String contentType, String value) {
        this.contentType = contentType;
        this.value = value;
    }

    public String getContentLength() {
        return String.valueOf(value.getBytes().length);
    }

    public String getContentType() {
        return contentType;
    }

    public String getValue() {
        return value;
    }
}
