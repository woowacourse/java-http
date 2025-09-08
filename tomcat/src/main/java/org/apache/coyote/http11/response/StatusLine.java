package org.apache.coyote.http11.response;

// <protocol> <status-code> <reason-phrase>
public record StatusLine(String protocol, HttpStatus status) {

    private static final String HTTP_1_1 = "HTTP/1.1";

    public StatusLine() {
        this(HTTP_1_1, HttpStatus.OK);
    }

    public StatusLine(String protocol, int statusCode) {
        this(protocol, HttpStatus.fromCode(statusCode));
    }

    public StatusLine(String protocol, HttpStatus status) {
        this.protocol = protocol;
        this.status = status;
    }

    public byte[] getBytes() {
        String sb = protocol + " "
                + status.getStatusCode() + " "
                + status.getReasonPhrase() + " \r\n";
        return sb.getBytes();
    }
}
