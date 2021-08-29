package nextstep.jwp.http.response;

import nextstep.jwp.http.response.type.StatusCode;

import java.nio.charset.StandardCharsets;

public class StatusLine {
    private String protocol;
    private String statusCode;
    private String statusMessage;

    public StatusLine(StatusCode statusCode) {
        this.protocol = "HTTP/1.1";
        this.statusCode = statusCode.getCode();
        this.statusMessage = statusCode.getMessage();
    }

    public byte[] getByte() {
        String line = String.join(" ", protocol, statusCode, statusMessage, "\r\n");
        return line.getBytes(StandardCharsets.UTF_8);
    }
}
