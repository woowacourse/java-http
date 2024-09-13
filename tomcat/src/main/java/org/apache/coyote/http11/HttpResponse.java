package org.apache.coyote.http11;

public class HttpResponse {

    private static final String CRLF = "\r\n";

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
        StringBuilder response = new StringBuilder(
                String.format("""
                                HTTP/1.1 %d %s\r
                                Content-Type: text/%s;charset=utf-8\r
                                Content-Length: %d\r
                                """,
                        httpStatus.getStatusCode(),
                        httpStatus.name(),
                        contentType,
                        responseBody.getBytes().length
                ));

        if (httpCookie != null) {
            response.append("Set-Cookie: ").append(httpCookie.getResponse()).append(CRLF);
        }

        if (location != null) {
            response.append("Location: ").append(location).append(CRLF);
        }

        response.append(CRLF).append(responseBody);
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

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getLocation() {
        return location;
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
