package org.apache.coyote.http11;

import java.util.StringJoiner;

public class HttpResponse {

    private final HttpStateCode stateCode;
    private final String location;
    private final MimeType mimeType;
    private final byte[] body;

    public HttpResponse(HttpStateCode stateCode, String location, MimeType mimeType, byte[] body) {
        this.stateCode = stateCode;
        this.location = location;
        this.mimeType = mimeType;
        this.body = body;
    }

    public HttpResponse(HttpStateCode stateCode, MimeType mimeType, byte[] body) {
        this(stateCode, null, mimeType, body);
    }

    public HttpResponse(HttpStateCode stateCode, byte[] body) {
        this(stateCode, null, MimeType.OTHER, body);
    }

    public HttpResponse(HttpStateCode stateCode, String location, MimeType mimeType) {
        this(stateCode, location, mimeType, null);
    }

    public byte[] toByte() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");

        stringJoiner.add("HTTP/1.1 " + stateCode.toStatus() + " ");
        if (stateCode.equals(HttpStateCode.FOUND)) {
            stringJoiner.add("Location: " + location);
            stringJoiner.add("Content-Type: " + mimeType.getValue() + " ");
            stringJoiner.add("\r\n");
            return stringJoiner.toString().getBytes();
        }
        stringJoiner.add("Content-Type: " + mimeType.getValue() + " ");
        stringJoiner.add("Content-Length: " + body.length + " ");
        stringJoiner.add("\r\n");

        byte[] headerBytes = stringJoiner.toString().getBytes();
        byte[] response = new byte[headerBytes.length + body.length];

        System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
        System.arraycopy(body, 0, response, headerBytes.length, body.length);

        return response;
    }
}
