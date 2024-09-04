package org.apache.coyote.http11;

import java.util.StringJoiner;

public class HttpResponse {

    private final HttpStatusCode stateCode;
    private final HttpHeader header;
    private final byte[] body;

    public HttpResponse(HttpStatusCode stateCode, String location, MimeType mimeType, byte[] body) {
        this.stateCode = stateCode;
        this.header = new HttpHeader();
        if (location != null) {
            header.setLocation(location);
        }
        if (mimeType != null) {
            header.setContentType(mimeType.getContentType());
        }
        this.body = body;
        if (body != null) {
            header.setContentLength(String.valueOf(body.length));
        }
    }

    public HttpResponse(HttpStatusCode stateCode, MimeType mimeType, byte[] body) {
        this(stateCode, null, mimeType, body);
    }

    public HttpResponse(HttpStatusCode stateCode, byte[] body) {
        this(stateCode, null, MimeType.OTHER, body);
    }

    public HttpResponse(HttpStatusCode stateCode, String location, MimeType mimeType) {
        this(stateCode, location, mimeType, null);
    }

    public byte[] toByte() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");

        stringJoiner.add("HTTP/1.1 " + stateCode.toStatus() + " ");
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
