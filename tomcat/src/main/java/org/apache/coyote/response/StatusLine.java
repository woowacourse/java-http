package org.apache.coyote.response;

public class StatusLine {

    private static final String SEPERATOR = " ";

    private String version;
    private HttpStatus httpStatus;

    public StatusLine(String version, HttpStatus httpStatus) {
        this.version = version;
        this.httpStatus = httpStatus;
    }

    public String getStatusLine() {
        return version + SEPERATOR + httpStatus.getHttpStatus() + SEPERATOR;
    }

    public void setStatus(HttpStatus status) {
        this.httpStatus = status;
    }
}
