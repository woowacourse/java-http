package nextstep.jwp.network;

public class HttpResponse {

    private final StatusLine statusLine;
    private final String contentType;
    private final String contentLength;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, byte[] bytes) {
        this(httpStatus, new String(bytes));
    }

    public HttpResponse(HttpStatus httpStatus, String body) {
        this.statusLine = new StatusLine(httpStatus);
        this.contentType = "Content-Type: text/html;charset=utf-8 ";
        this.contentLength = "Content-Length: " + body.getBytes().length + " ";
        this.body = body;
    }

    public String asString() {
        return String.join("\r\n",
                statusLine.asString(),
                contentType,
                contentLength,
                "",
                body);
    }

//    class Builder {
//        private final StatusLine statusLine;
//        private final String contentType;
//        private final String contentLength;
//        private final String body;
//
//    }
}

