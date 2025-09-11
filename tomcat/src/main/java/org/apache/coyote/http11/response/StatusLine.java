package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.domain.HttpProtocol;

public class StatusLine {

    private final HttpProtocol protocol;
    private HttpStatus status;

    public StatusLine() {
        this(HttpProtocol.HTTP1_1, HttpStatus.OK);
    }

    public StatusLine(HttpProtocol protocol, HttpStatus status) {
        this.protocol = protocol;
        this.status = status;
    }

    public byte[] getBytes() {
        String sb = protocol.toString() + " "
                + status.getStatusCode() + " "
                + status.getReasonPhrase() + " \r\n";

        return sb.getBytes(StandardCharsets.UTF_8);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
