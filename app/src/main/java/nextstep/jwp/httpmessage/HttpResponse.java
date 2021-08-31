package nextstep.jwp.httpmessage;

public class HttpResponse {

    private final StatusLine statusLine;

    public HttpResponse(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public String getStatusLine() {
        return statusLine.getLine();
    }

    public HttpVersion getHttpVersion() {
        return statusLine.getHttpVersion();
    }

    public HttpStatusCode getHttpStatusCode() {
        return statusLine.getHttpStatusCode();
    }
}
