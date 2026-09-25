package org.apache.coyote.http11;

public class StatusLine {
    private final String protocol_version;
    private final String status_code;
    private final String status_message;

    public StatusLine(String protocol_version, String status_code, String status_message) {
        this.protocol_version = protocol_version;
        this.status_code = status_code;
        this.status_message = status_message;
    }

    public String toLine() {
        return protocol_version + " " + status_code + " " + status_message;
    }
}
