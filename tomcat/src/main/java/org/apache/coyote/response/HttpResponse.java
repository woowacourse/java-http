package org.apache.coyote.response;

public class HttpResponse {

    private final String contentType;
    private final String responseBody;

    public HttpResponse(final String contentType, final String responseBody) {
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public byte[] getResponse() {
        return createResponse().getBytes();
    }

    private String createResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK " +
                        String.format("Content-Type: %s;charset=utf-8 ", contentType),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
