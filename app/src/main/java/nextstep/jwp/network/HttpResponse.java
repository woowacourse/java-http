package nextstep.jwp.network;


public class HttpResponse {

    private final StatusLine statusLine;
    private final ContentType contentType;
    private final int contentLength;
    private final String body;

    private HttpResponse(HttpStatus httpStatus, ContentType contentType, String body) {
        this.statusLine = new StatusLine(httpStatus);
        this.contentType = contentType;
        this.contentLength = body.getBytes().length;
        this.body = body;
    }

    public static HttpResponse ofByteArray(HttpStatus httpStatus, byte[] body) {
        return new HttpResponse(httpStatus, ContentType.HTML, new String(body));
    }

    public static HttpResponse ofByteArray(HttpStatus httpStatus, ContentType contentType, byte[] body) {
        return new HttpResponse(httpStatus, contentType, new String(body));
    }

    public static HttpResponse ofString(HttpStatus httpStatus, ContentType contentType, String body) {
        return new HttpResponse(httpStatus, contentType, body);
    }

    public String asString() {
        return String.join("\r\n",
                statusLine.asString(),
                "Content-Type: " + contentType.getType() + " ",
                "Content-Length: " + contentLength + " ",
                "",
                body);
    }
}

