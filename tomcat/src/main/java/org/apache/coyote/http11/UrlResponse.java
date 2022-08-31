package org.apache.coyote.http11;

public class UrlResponse {

    private final String responseBody;
    private final String contentType;

    public UrlResponse(String responseBody, String contentType) {
        this.responseBody = responseBody;
        this.contentType = contentType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getContentType() {
        return contentType;
    }
}
