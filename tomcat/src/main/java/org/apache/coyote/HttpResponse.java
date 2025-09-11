package org.apache.coyote;

import java.util.Map;

public class HttpResponse {

    private final String protocol;
    private final HttpStatus status;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(final String protocol, final HttpStatus status, final Map<String, String> headers, final byte[] body) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

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
}
