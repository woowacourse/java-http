package org.apache.coyote.http11.response;

import org.apache.coyote.http11.domain.HttpProtocol;

public record StatusLine(HttpProtocol protocol, HttpStatus status) {

    public StatusLine() {
        this(HttpProtocol.HTTP1_1, HttpStatus.OK);
    }

    public StatusLine(String protocol, int statusCode) {
        this(HttpProtocol.valueOf(protocol), HttpStatus.fromCode(statusCode));
    }

    public byte[] getBytes() {
        String sb = protocol + " "
                + status.getStatusCode() + " "
                + status.getReasonPhrase() + " \r\n";
        return sb.getBytes();
    }
}
