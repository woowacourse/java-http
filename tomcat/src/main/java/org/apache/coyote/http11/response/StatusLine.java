package org.apache.coyote.http11.response;

public class StatusLine {

    private final String protocol;
    //    private final String statusCode;
//    private final String statusMessage;
    private final Status status;

//    public StatusLine(String protocol, String statusCode, String statusMessage) {
//        this.protocol = protocol;
//        this.statusCode = statusCode;
//        this.statusMessage = statusMessage;
//    }

    public StatusLine(String protocol, Status status) {
        this.protocol = protocol;
        this.status = status;
    }

    //    public String getStatusLine() {
//        return protocol + " " + statusCode + " " + statusMessage;
//    }
    public String getStatusLine() {
        return protocol + " " + status.getCode() + " " + status.getMessage();
    }
}
