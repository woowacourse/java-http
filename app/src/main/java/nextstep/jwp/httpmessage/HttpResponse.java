package nextstep.jwp.httpmessage;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;

    public HttpResponse(StatusLine statusLine, HttpHeaders httpHeaders) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
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

    public void setHeader(String key, String value) {
        httpHeaders.setHeader(key, value);
    }

    public String getHeader(String key) {
        return httpHeaders.getHeader(key);
    }
}
