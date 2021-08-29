package nextstep.jwp.http;

public enum HttpStatusCodes {
    _200("HTTP/1.1 200 OK\r\n"),
    _302("HTTP/1.1 302 Found\r\n"),
    _401("HTTP/1.1 401 Unauthorized\r\n"),
    _404("HTTP/1.1 404 Not Found\r\n"),
    _409("HTTP/1.1 409 Conflict\r\n");

    private final String statusCodeHeader;

    HttpStatusCodes(final String statusCodeHeader) {
        this.statusCodeHeader = statusCodeHeader;
    }

    public String getStatusCodeHeader() {
        return statusCodeHeader;
    }
}
