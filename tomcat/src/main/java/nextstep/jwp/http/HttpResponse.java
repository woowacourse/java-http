package nextstep.jwp.http;

public class HttpResponse {

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;

    private HttpResponse(StatusCode statusCode, ContentType contentType, String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(StatusCode statusCode, ContentType contentType,
                                  String responseBody) {
        return new HttpResponse(statusCode, contentType, responseBody);
    }

    public byte[] writeResponse() {
        return String.join("\r\n",
            "HTTP/1.1 " + statusCode.getStatus() + " ",
            "Content-Type: " + contentType.getMediaType() + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody).getBytes();
    }
}
