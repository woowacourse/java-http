package nextstep.jwp;

public class HttpResponse {

    private final StatusLine statusLine;
    private final String contentType;
    private final String contentLength;
    private final String body;

    public HttpResponse(byte[] bytes) {
        this(new String(bytes));
    }

    public HttpResponse(String body) {
        this.statusLine = new StatusLine();
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
}

