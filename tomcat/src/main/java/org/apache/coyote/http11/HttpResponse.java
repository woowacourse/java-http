package org.apache.coyote.http11;

import java.util.StringJoiner;

public class HttpResponse {

    private final HttpStateCode stateCode;
    private final byte[] body;

    public HttpResponse(HttpStateCode stateCode, byte[] body) {
        this.stateCode = stateCode;
        this.body = body;
    }

    public byte[] toByte() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add("HTTP/1.1 " + stateCode.toStatus() + " ");
        stringJoiner.add("Content-Type: text/html;charset=utf-8" + " ");
        stringJoiner.add("Content-Length: " + body.length + " ");
        stringJoiner.add("\r\n");

        byte[] headerBytes = stringJoiner.toString().getBytes();
        byte[] response = new byte[headerBytes.length + body.length];

        System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
        System.arraycopy(body, 0, response, headerBytes.length, body.length);

        return response;
    }
}
