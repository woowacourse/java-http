package nextstep.jwp.model.web.response;

public class StatusLine {

    private String versionOfControl;
    private int statusCode;
    private String statusMessage;

    public StatusLine(String versionOfControl, int statusCode, String statusMessage) {
        this.versionOfControl = versionOfControl;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
