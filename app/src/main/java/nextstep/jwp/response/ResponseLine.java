package nextstep.jwp.response;

public class ResponseLine {

    private static final String VERSION_OF_HTTP = "HTTP/1.1";
    private static final String SPACE = " ";

    private final String versionOfProtocol;
    private final HttpStatusCode statusCode;

    public ResponseLine(String versionOfProtocol, HttpStatusCode statusCode) {
        this.versionOfProtocol = versionOfProtocol;
        this.statusCode = statusCode;
    }

    public static ResponseLine httpResponseLine(HttpStatusCode statusCode) {
        return new ResponseLine(VERSION_OF_HTTP, statusCode);
    }

    public String toStatusLine() {
        return versionOfProtocol +
                SPACE +
                statusCode.getStatusCode() +
                SPACE +
                statusCode.getStatusMessage() +
                SPACE;
    }
}
