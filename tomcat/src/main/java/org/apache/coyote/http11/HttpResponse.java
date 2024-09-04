package org.apache.coyote.http11;

public class HttpResponse {

    private final String responseBody;

    public HttpResponse(String responseBody) {
        this.responseBody = responseBody;
    }

    public byte[] getBytes() {
        String response = createResponse();
        return response.getBytes();
    }

    private String createResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
