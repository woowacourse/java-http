package org.apache.coyote.http11;

public class HttpResponse {

    private String responseBody;
    private String contentType;
    private String resourceName;
    private HttpStatus httpStatus;

    public HttpResponse() {
        httpStatus = HttpStatus.OK;
    }

    public byte[] getBytes() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.name() + " ",
                "Content-Type: text/" + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        ).getBytes();
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

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
