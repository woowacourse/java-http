package org.apache.coyote.http11;

public class Http11Response {

    private static final String RESOURCE_SEPARATOR = "/";
    private static final String HTML_EXTENSION = ".html";

    private String resourceURI;
    private String httpStatusCode;
    private String location;
    private String contentType;
    private String responseBody;

    public String getString() {
        return null;
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getLocation() {
        return location;
    }

    public String getContentType() {
        return contentType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResourceURI(final String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public void setStatusCode(final String value) {
        this.httpStatusCode = value;
    }

    public void setLocation(final String location) {
        this.location = RESOURCE_SEPARATOR + location + HTML_EXTENSION;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }
}
