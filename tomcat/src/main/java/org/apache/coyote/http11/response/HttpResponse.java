package org.apache.coyote.http11.response;

public class HttpResponse {

    private final String protocol;
    private final HttpStatus status;
    private final ContentType contentType;
    private final String responseBody;

    public HttpResponse(String protocol,
                        HttpStatus status,
                        ContentType contentType,
                        String responseBody) {
        this.protocol = protocol;
        this.status = status;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public String parseToString() {
        return String.join("\r\n",
                protocol + " " + status.getCode() + " ",
                "Content-Type: " + contentType.getType() + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "protocol='" + protocol + '\'' +
                ", status='" + status + '\'' +
                ", contentType='" + contentType + '\'' +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}
