package org.apache.coyote.http11;

public class HttpResponse {
    private final String version;
    private final String status;
    private final String contentType;
    private final String responseBody;

    public HttpResponse(String version, String status, String uri, String responseBody) {
        this.version = version;
        this.status = status;
        this.responseBody = responseBody;

        if (uri.endsWith("css")) {
            this.contentType = "text/css,*/*;q=0.1";
        } else {
            this.contentType = "text/html;charset=utf-8";
        }
    }

    public String toResponseString() {
        StringBuilder response = new StringBuilder();

        response.append(version + " " + status + " \r\n");
        response.append("Content-Type: " + contentType + " \r\n");
        response.append("Content-Length: " + responseBody.getBytes().length + " \r\n");
        response.append("\r\n");
        response.append(responseBody);

        return response.toString();
    }
}
