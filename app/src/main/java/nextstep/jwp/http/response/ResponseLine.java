package nextstep.jwp.http.response;

public class ResponseLine {
    private String version;
    private HttpResponseStatus httpResponseStatus;

    public ResponseLine(String version) {
        this.version = version;
    }

    public void status(HttpResponseStatus status) {
        this.httpResponseStatus = status;
    }

    public String getVersion() {
        return version;
    }

    public boolean isOk() {
        return httpResponseStatus.equals(HttpResponseStatus.OK);
    }

    public String asLine() {
        return version + " " + httpResponseStatus.getLine() + "\r\n";
    }

    public boolean isStatusEmpty() {
        return httpResponseStatus == null;
    }
}
