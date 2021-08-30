package nextstep.jwp.response;

public class ResponseLine {

    private static final String SPACE = " ";

    private final String versionOfProtocol;
    private final HttpStatusCode statusCode;

    public ResponseLine(String versionOfProtocol, HttpStatusCode statusCode) {
        this.versionOfProtocol = versionOfProtocol;
        this.statusCode = statusCode;
    }

    public String toResponseLine() {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append(versionOfProtocol)
                .append(SPACE)
                .append(statusCode.getStatusCode())
                .append(SPACE)
                .append(statusCode.getStatusMessage())
                .toString();
    }
}
