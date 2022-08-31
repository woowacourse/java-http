package org.apache.coyote.http11;

public class HttpResponse {
    private final String httpMethod;
    private final String statusCode;
    private final String contentType;
    private final int contentLength;
    private final String responseBody;

    private HttpResponse(String httpMethod, String statusCode, String contentType, int contentLength,
        String responseBody) {
        this.httpMethod = httpMethod;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.responseBody = responseBody;
    }

    public static HttpResponse from(String httpMethod, String statusCode, String contentType, String responseBody) {
        return new HttpResponse(httpMethod, statusCode, contentType, responseBody.getBytes().length, responseBody);
    }

    public byte[] getBytes() {
        String response = String.join("\r\n",
            httpMethod + " " + statusCode + " ",
            "Content-Type: " + contentType + " ",
            "Content-Length: " + contentLength + " ",
            "",
            responseBody);

        return response.getBytes();
    }
}
