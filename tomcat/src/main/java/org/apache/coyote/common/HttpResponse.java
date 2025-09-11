package org.apache.coyote.common;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private String protocol;
    private HttpStatus status;
    private Map<String, String> headers;
    private byte[] body;

    public byte[] getBytes() {
        String message = getMessage();

        if (body == null) {
            return message.getBytes();
        }

        byte[] messageBytes = message.getBytes();
        byte[] result = new byte[messageBytes.length + body.length];

        System.arraycopy(messageBytes, 0, result, 0, messageBytes.length);
        System.arraycopy(body, 0, result, messageBytes.length, body.length);

        return result;
    }

    private String getMessage() {
        StringBuilder builder = new StringBuilder();

        builder.append(protocol).append(" ").append(status.getCode()).append(" ").append(status.getMessage()).append(" \r\n");

        if (headers != null) {
            headers.forEach((key, value) -> builder.append(key).append(": ").append(value).append(" \r\n"));
        }
        if (body == null) {
            return builder.toString();
        }

        builder.append("Content-Length: ").append(body.length).append(" \r\n");
        builder.append("\r\n");

        return builder.toString();
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void setHeaders(final Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public void setBody(final byte[] body) {
        this.body = new byte[body.length];
        System.arraycopy(body, 0, this.body, 0, body.length);
    }
}
