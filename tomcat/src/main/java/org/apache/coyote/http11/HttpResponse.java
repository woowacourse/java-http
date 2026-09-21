package org.apache.coyote.http11;

public class HttpResponse {
    private String status;
    private String message;
    private String contentType;
    private String responseBody;

    private HttpResponse(String status, String message, String contentType, String responseBody) {
        this.status = status;
        this.message = message;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse isOk(String contentType, String responseBody) {
        return new HttpResponse("200", "OK", contentType, responseBody);
    }

    public String convertString(){
        return String.join("\r\n",
                "HTTP/1.1 " + status + " " + message + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
