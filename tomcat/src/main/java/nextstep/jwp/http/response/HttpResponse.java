package nextstep.jwp.http.response;

public class HttpResponse {

    private String version;
    private String statusCode;
    private String statusMessage;
    private String contentType;
    private int contentLength;
    private String responseBody;

    public HttpResponse(final String version, final String statusCode, final String statusMessage,
                        final String contentType, final int contentLength, final String responseBody) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.responseBody = responseBody;
    }

    public String getTemplate() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + this.contentType + ";charset=utf-8 ",
                "Content-Length: " + this.responseBody.getBytes().length + " ",
                "",
                this.responseBody);
    }
}
