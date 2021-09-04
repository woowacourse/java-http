package nextstep.jwp.http;

public enum HttpStatusCodes {
    HTTP_200("HTTP/1.1 200 OK\r\n"),
    HTTP_302("HTTP/1.1 302 Found\r\n"),
    HTTP_401("HTTP/1.1 401 Unauthorized\r\n"),
    HTTP_404("HTTP/1.1 404 Not Found\r\n"),
    HTTP_409("HTTP/1.1 409 Conflict\r\n");

    private final String statusCodeHeader;

    HttpStatusCodes(final String statusCodeHeader) {
        this.statusCodeHeader = statusCodeHeader;
    }

    public String getStatusCodeHeader() {
        return statusCodeHeader;
    }
}
