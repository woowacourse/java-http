package org.apache.coyote.http11;

public class HttpResponse {

    private final String responseBody;
    private final String contentType;

    public HttpResponse(String responseBody, String contentType) {
        this.responseBody = responseBody;
        this.contentType = contentType;
    }

    public byte[] getBytes() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        ).getBytes();
    }
}
