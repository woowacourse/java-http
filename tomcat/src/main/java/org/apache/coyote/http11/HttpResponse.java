package org.apache.coyote.http11;

public class HttpResponse {
    private String statusCode;
    private String statusText;
    private String contentType;
    private String responseBody;
    private String location;

    public HttpResponse(String statusCode, String statusText, String contentType, String responseBody) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public HttpResponse(String statusCode, String statusText, String location) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.location = location;
    }

    public static HttpResponse of(HttpRequests httpRequests, String responseBody) {
        return new HttpResponse(httpRequests.getStatusCode(), httpRequests.getStatusText(), httpRequests.getContentType(), responseBody);
    }

    public static HttpResponse ofLoginRedirect(HttpRequests httpRequests) {
        return new HttpResponse(httpRequests.getStatusCode(), httpRequests.getStatusText(), httpRequests.getFileName());
    }

    public String serialize() {
        if (location != null) {
            return String.join("\r\n",
                    "HTTP/1.1 " + this.statusCode + " " + this.statusText + " ",
                    "Location: " + this.location + " ",
                    "");
        }
        return String.join("\r\n",
                "HTTP/1.1 " + this.statusCode + " " + this.statusText + " ",
                "Content-Type: " + this.contentType + ";charset=utf-8 ",
                "Content-Length: " + this.responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
