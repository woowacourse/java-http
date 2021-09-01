package nextstep.jwp.model.web.response;

public class StatusLine {

    private String method;
    private String path;
    private String versionOfControl;

    public StatusLine(String method, String path, String versionOfControl) {
        this.method = method;
        this.path = path;
        this.versionOfControl = versionOfControl;
    }
}
