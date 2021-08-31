package nextstep.jwp;

import java.util.Arrays;

public class ResponseLine {
    private final String version;
    private final String statusCode;
    private final String statusMessage;

    public ResponseLine(String version, String statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public ResponseLine(String statusCode, String statusMessage) {
        this("HTTP/1.1", statusCode, statusMessage);
    }

    public String getResponseLine() {
        return String.join(" ", Arrays.asList(version, statusCode, statusMessage)) + " ";
    }
}
