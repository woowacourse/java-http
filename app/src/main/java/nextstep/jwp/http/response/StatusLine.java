package nextstep.jwp.http.response;

import nextstep.jwp.http.response.status.HttpStatus;

public class StatusLine {

    private String versionOfProtocol;
    private HttpStatus httpStatus;

    public StatusLine(String versionOfProtocol, HttpStatus httpStatus) {
        this.versionOfProtocol = versionOfProtocol;
        this.httpStatus = httpStatus;
    }

    public String getVersionOfProtocol() {
        return versionOfProtocol;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String asString() {
        return String.format("%s %s %s ",
                versionOfProtocol,
                httpStatus.getValue(),
                httpStatus.getReasonPhrase()
        );
    }
}
