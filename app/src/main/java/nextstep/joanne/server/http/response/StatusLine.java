package nextstep.joanne.server.http.response;

import nextstep.joanne.server.http.HttpStatus;

public class StatusLine {
    public static final String VERSION_OF_PROTOCOL = "HTTP/1.1";
    private final String version;
    private final String statusCode;
    private final String statusMessage;

    public StatusLine(HttpStatus httpStatus) {
        this(VERSION_OF_PROTOCOL, httpStatus.value(), httpStatus.responsePhrase());
    }

    public StatusLine(String version, String statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getVersion() {
        return version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
