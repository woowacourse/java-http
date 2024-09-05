package org.apache.coyote.http11.response;

import java.util.StringJoiner;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.HttpStatusCode;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader header;
    private final byte[] body;

    public HttpResponse(HttpStatusCode statusCode, ResponseHeader header) {
        this(statusCode, header, null);
    }

    public HttpResponse(HttpStatusCode statusCode, ResponseHeader header, byte[] body) {
        this.statusLine = new StatusLine(HttpVersion.HTTP_1_1, statusCode);
        this.header = header;
        this.body = body;
        if (body != null) {
            header.setContentLength(String.valueOf(body.length));
        }
    }

    public byte[] toByte() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");

        stringJoiner.add(statusLine.getReponseString());
        stringJoiner.add(header.toHeaderString());
        stringJoiner.add("\r\n");

        byte[] headerBytes = stringJoiner.toString().getBytes();
        if (body != null) {
            byte[] response = new byte[headerBytes.length + body.length];

            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(body, 0, response, headerBytes.length, body.length);

            return response;
        }
        return headerBytes;
    }
}
