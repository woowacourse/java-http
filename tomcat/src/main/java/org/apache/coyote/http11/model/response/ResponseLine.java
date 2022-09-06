package org.apache.coyote.http11.model.response;

public class ResponseLine {

    private final String version;
    private final ResponseStatusCode statusCode;

    public ResponseLine(final String version, final ResponseStatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public static ResponseLine of(final ResponseStatusCode statusCode, final String version) {
        return new ResponseLine(version, statusCode);
    }

    public String getResponseLine() {
        return version + " " + statusCode.getStatusCode() + " " +statusCode.name()  + " ";
    }
}
