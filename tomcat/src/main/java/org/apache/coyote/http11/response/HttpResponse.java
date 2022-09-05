package org.apache.coyote.http11.response;

public class HttpResponse {

    private final String protocol;
    private final HttpStatus status;
    private final String location;
    private final ContentType contentType;
    private final String responseBody;

    public HttpResponse(String protocol,
                        HttpStatus status,
                        ContentType contentType,
                        String responseBody) {
        this.protocol = protocol;
        this.status = status;
        this.location = null;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public HttpResponse(String protocol,
                        HttpStatus status,
                        String location) {
        this.protocol = protocol;
        this.status = status;
        this.location = location;
        this.contentType = null;
        this.responseBody = null;
    }

    public String parseToString() {
        if (contentType == null && responseBody == null) {
            return String.join("\r\n",
                    protocol + " " + status.getCode() + " " + status.getText() + " ",
                    "Location: " + location + " ",
                    "");
        }
        return String.join("\r\n",
                protocol + " " + status.getCode() + " " + status.getText() + " ",
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
