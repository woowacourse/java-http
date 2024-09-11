package org.apache.coyote.http11;

public class HttpResponse {

    private HttpStatus httpStatus;
    private String contentType;
    private String resourceName;
    private HttpCookie httpCookie;
    private String location;
    private String responseBody;

    public HttpResponse() {
        httpStatus = HttpStatus.OK;
    }

    public byte[] getBytes() {
        StringBuilder response = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(httpStatus.getStatusCode()).append(" ").append(httpStatus.name()).append(" \r\n")
                .append("Content-Type: text/").append(contentType).append(";charset=utf-8 \r\n")
                .append("Content-Length: ").append(responseBody.getBytes().length).append(" \r\n");

        if (httpCookie != null) {
            response.append("Set-Cookie: ").append(httpCookie.getResponse()).append(" \r\n");
        }

        if (location != null) {
            response.append("Location: ").append(location).append(" \r\n");
        }

        response.append("\r\n").append(responseBody);
        return response.toString().getBytes();
    }

    public void redirectPage(String path) {
        this.location = path;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setHttpCookie(HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
    }

    public void ok() {
        this.httpStatus = HttpStatus.OK;
    }

    public void found() {
        this.httpStatus = HttpStatus.FOUND;
    }
}
